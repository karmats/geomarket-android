package com.geomarket.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.geomarket.android.R;
import com.geomarket.android.util.LogHelper;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;

import java.io.IOException;

/**
 * A base class to wrap communication with the Google Play Services PlusClient.
 */
public abstract class PlusBaseActivity extends Activity
        implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    // A magic number we will use to know that our sign-in error resolution activity has completed
    private static final int OUR_REQUEST_CODE = 49404;
    // Request code for attempting a one time token from google+
    static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 200;

    public static final String SHARED_PREFS_NAME = "GeoMarketPreferences";

    // Shared preferences boolean if user logged in.
    // TODO Should not be in production
    static final String PREF_IS_LOGGED_IN = "isLoggedIn";

    // A flag to stop multiple dialogues appearing for the user
    private boolean mAutoResolveOnFail;

    // A flag to track when a connection is already in progress
    public boolean mPlusClientIsConnecting = false;

    private boolean mTokenRequestActive = false;

    // This is the helper object that connects to Google Play Services.
    private PlusClient mPlusClient;

    // The saved result from {@link #onConnectionFailed(ConnectionResult)}.  If a connection
    // attempt has been made, this is non-null.
    // If this IS null, then the connect method is still running.
    private ConnectionResult mConnectionResult;


    /**
     * Called when the {@link PlusClient} revokes access to this app.
     */
    protected abstract void onPlusClientRevokeAccess();

    /**
     * Called when the PlusClient is successfully connected.
     */
    protected abstract void onPlusClientSignIn();

    /**
     * Called when the {@link PlusClient} is disconnected.
     */
    protected abstract void onPlusClientSignOut();

    /**
     * Called when the {@link PlusClient} is blocking the UI.  If you have a progress bar widget,
     * this tells you when to show or hide it.
     */
    protected abstract void onPlusClientBlockingUI(boolean show);

    /**
     * Called when there is a change in connection state.  If you have "Sign in"/ "Connect",
     * "Sign out"/ "Disconnect", or "Revoke access" buttons, this lets you know when their states
     * need to be updated.
     */
    protected abstract void updateConnectButtonState();

    /**
     * Called when the one time use plus token is fetched from google+ service
     *
     * @param token The one time token to use server side
     */
    protected abstract void onPlusTokenFetched(String token);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the PlusClient connection.
        // Scopes indicate the information about the user your application will be able to access.
        mPlusClient =
                new PlusClient.Builder(this, this, this).setScopes(Scopes.PLUS_LOGIN).build();
    }

    /**
     * Try to sign in the user.
     */
    public void signIn() {
        if (!mPlusClient.isConnected()) {
            // Show the dialog as we are now signing in.
            setProgressBarVisible(true);
            // Make sure that we will start the resolution (e.g. fire the intent and pop up a
            // dialog for the user) for any errors that come in.
            mAutoResolveOnFail = true;
            // We should always have a connection result ready to resolve,
            // so we can start that process.
            if (mConnectionResult != null) {
                startResolution();
            } else {
                // If we don't have one though, we can start connect in
                // order to retrieve one.
                initiatePlusClientConnect();
            }
        }

        updateConnectButtonState();
    }

    /**
     * Connect the {@link PlusClient} only if a connection isn't already in progress.  This will
     * call back to {@link #onConnected(android.os.Bundle)} or
     * {@link #onConnectionFailed(com.google.android.gms.common.ConnectionResult)}.
     */
    private void initiatePlusClientConnect() {
        if (!mPlusClient.isConnected() && !mPlusClient.isConnecting()) {
            mPlusClient.connect();
        }
    }

    /**
     * Disconnect the {@link PlusClient} only if it is connected (otherwise, it can throw an error.)
     * This will call back to {@link #onDisconnected()}.
     */
    private void initiatePlusClientDisconnect() {
        if (mPlusClient.isConnected()) {
            mPlusClient.disconnect();
        }
    }

    /**
     * Sign out the user (so they can switch to another account).
     */
    public void signOut() {

        // We only want to sign out if we're connected.
        if (mPlusClient.isConnected()) {
            // Clear the default account in order to allow the user to potentially choose a
            // different account from the account chooser.
            mPlusClient.clearDefaultAccount();

            // Disconnect from Google Play Services, then reconnect in order to restart the
            // process from scratch.
            initiatePlusClientDisconnect();

            getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE).edit().putBoolean(PREF_IS_LOGGED_IN, false).commit();
            LogHelper.logDebug("Sign out successful!");
        }

        updateConnectButtonState();
    }

    /**
     * Revoke Google+ authorization completely.
     */
    public void revokeAccess() {

        if (mPlusClient.isConnected()) {
            // Clear the default account as in the Sign Out.
            mPlusClient.clearDefaultAccount();
            // TODO Not like this
            getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE).edit().putBoolean(PREF_IS_LOGGED_IN, false).commit();

            // Revoke access to this entire application. This will call back to
            // onAccessRevoked when it is complete, as it needs to reach the Google
            // authentication servers to revoke all tokens.
            mPlusClient.revokeAccessAndDisconnect(new PlusClient.OnAccessRevokedListener() {
                public void onAccessRevoked(ConnectionResult result) {
                    updateConnectButtonState();
                    onPlusClientRevokeAccess();
                }
            });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        initiatePlusClientConnect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        initiatePlusClientDisconnect();
    }

    public boolean isPlusClientConnecting() {
        return mPlusClientIsConnecting;
    }

    private void setProgressBarVisible(boolean flag) {
        mPlusClientIsConnecting = flag;
        onPlusClientBlockingUI(flag);
    }

    /**
     * A helper method to flip the mResolveOnFail flag and start the resolution
     * of the ConnectionResult from the failed connect() call.
     */
    private void startResolution() {
        try {
            // Don't start another resolution now until we have a result from the activity we're
            // about to start.
            mAutoResolveOnFail = false;
            // If we can resolve the error, then call start resolution and pass it an integer tag
            // we can use to track.
            // This means that when we get the onActivityResult callback we'll know it's from
            // being started here.
            mConnectionResult.startResolutionForResult(this, OUR_REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            // Any problems, just try to connect() again so we get a new ConnectionResult.
            mConnectionResult = null;
            initiatePlusClientConnect();
        }
    }

    /**
     * An earlier connection failed, and we're now receiving the result of the resolution attempt
     * by PlusClient.
     *
     * @see #onConnectionFailed(ConnectionResult)
     */
    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        updateConnectButtonState();
        if (requestCode == OUR_REQUEST_CODE && responseCode == RESULT_OK) {
            // If we have a successful result, we will want to be able to resolve any further
            // errors, so turn on resolution with our flag.
            mAutoResolveOnFail = true;
            // If we have a successful result, let's call connect() again. If there are any more
            // errors to resolve we'll get our onConnectionFailed, but if not,
            // we'll get onConnected.
            initiatePlusClientConnect();
        } else if (requestCode == OUR_REQUEST_CODE && responseCode != RESULT_OK) {
            // If we've got an error we can't resolve, we're no longer in the midst of signing
            // in, so we can stop the progress spinner.
            setProgressBarVisible(false);
        } else if (requestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR) {
            // user has returned back from the permissions screen,
            // if he/she has given enough permissions, retry the the request.
            if (responseCode == RESULT_OK) {
                Bundle extra = intent.getExtras();
                String oneTimeToken = extra.getString("authtoken");
                // TODO Remove with logic to backend
                if (oneTimeToken != null) {
                    onPlusTokenFetched(oneTimeToken);
                    getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE).edit().putBoolean(PREF_IS_LOGGED_IN, true).commit();
                } else {
                    getToken();
                }
            } else if (responseCode == RESULT_CANCELED) {
                // User cancelled operation
                mTokenRequestActive = false;
                Toast.makeText(this, R.string.pick_account, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Get one time token from google+ to sign in server side.
    private void getToken() {
        boolean loggedIn = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE).getBoolean(PREF_IS_LOGGED_IN, false);
        LogHelper.logInfo("Logged in? " + loggedIn);
        if (!loggedIn && !mTokenRequestActive) {
            mTokenRequestActive = true;
            new GetTokenTask(this, getPlusClient().getAccountName()).execute();
        }
    }


    /**
     * Successfully connected (called by PlusClient)
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Get a one time token and sign in to dibbler
        getToken();
        updateConnectButtonState();
        setProgressBarVisible(false);
        onPlusClientSignIn();
    }

    /**
     * Successfully disconnected (called by PlusClient)
     */
    @Override
    public void onDisconnected() {
        getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE).edit().putBoolean(PREF_IS_LOGGED_IN, false).commit();
        updateConnectButtonState();
        onPlusClientSignOut();
    }

    /**
     * Connection failed for some reason (called by PlusClient)
     * Try and resolve the result.  Failure here is usually not an indication of a serious error,
     * just that the user's input is needed.
     *
     * @see #onActivityResult(int, int, Intent)
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        updateConnectButtonState();

        // Most of the time, the connection will fail with a user resolvable result. We can store
        // that in our mConnectionResult property ready to be used when the user clicks the
        // sign-in button.
        if (result.hasResolution()) {
            mConnectionResult = result;
            if (mAutoResolveOnFail) {
                // This is a local helper function that starts the resolution of the problem,
                // which may be showing the user an account chooser or similar.
                startResolution();
            }
        }
    }

    public PlusClient getPlusClient() {
        return mPlusClient;
    }

    /**
     * Get a one time token from Google+ oauth2
     */
    public class GetTokenTask extends AsyncTask<Void, Void, String> {
        private static final String CLIENT_ID = "446637837872-ctf49ogkcrk0dmgemff8j6ck9guii73q.apps.googleusercontent.com";

        private PlusBaseActivity mActivity;
        private String mAccountName;

        GetTokenTask(PlusBaseActivity activity, String name) {
            this.mActivity = activity;
            this.mAccountName = name;
        }

        /**
         * Executes the asynchronous job. This runs when you call execute()
         * on the AsyncTask instance.
         */
        @Override
        protected String doInBackground(Void... params) {
            try {
                String token = fetchToken();
                // TODO Replace with call to backend
                getSharedPreferences(LoginActivity.SHARED_PREFS_NAME, MODE_PRIVATE).edit().putBoolean(LoginActivity.PREF_IS_LOGGED_IN, true);
                return token;
            } catch (IOException e) {
                // The fetchToken() method handles Google-specific exceptions,
                // so this indicates something went wrong at a higher level.
                // TIP: Check for network connectivity before starting the AsyncTask.
            }
            return null;
        }

        @Override
        protected void onPostExecute(String token) {
            mActivity.onPlusTokenFetched(token);
        }

        /**
         * Gets an authentication token from Google and handles any
         * GoogleAuthException that may occur.
         */
        protected String fetchToken() throws IOException {
            String scope = "oauth2:server:client_id:" + CLIENT_ID + ":api_scope:" + Scopes.PLUS_LOGIN;
            LogHelper.logInfo(scope);
            try {
                return GoogleAuthUtil.getToken(mActivity, mAccountName, scope);
            } catch (UserRecoverableAuthException e) {
                // GooglePlayServices.apk is either old, disabled, or not present
                // so we need to show the user some UI in the activity to recover.
                LogHelper.logException(e);
                startActivityForResult(e.getIntent(), REQUEST_CODE_RECOVER_FROM_AUTH_ERROR);
            } catch (GoogleAuthException e) {
                // Some other type of unrecoverable exception has occurred.
                // Report and log the error as appropriate for your app.
                LogHelper.logException(e);
            }
            return null;
        }
    }
}
