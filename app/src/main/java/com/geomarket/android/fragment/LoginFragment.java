package com.geomarket.android.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.geomarket.android.R;
import com.geomarket.android.util.LogHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A login screen that offers login via Google+ sign in.
 * <p/>
 */
public class LoginFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, Session.StatusCallback {

    // A magic number we will use to know that our sign-in error resolution activity has completed
    private static final int RC_SIGN_IN = 49404;

    // Client used to interact with Google APIs.
    private GoogleApiClient mGoogleApiClient;

    /* A flag indicating that a PendingIntent is in progress and prevents
     * us from starting further intents.
     */
    private boolean mIntentInProgress;

    // UI references.
    @InjectView(R.id.login_progress)
    View mProgressView;
    @InjectView((R.id.plus_sign_in_button))
    SignInButton mPlusSignInButton;
    @InjectView(R.id.facebook_sign_in_button)
    LoginButton mFacebookLoginButton;
    @InjectView(R.id.login_form)
    View mLoginFormView;
    @InjectView(R.id.user_info)
    View mUserInfoView;
    @InjectView(R.id.user_info_name)
    TextView mUserInfoName;
    @InjectView(R.id.user_info_id)
    TextView mUserInfoId;
    @InjectView(R.id.user_info_gender)
    TextView mUserInfoGender;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the PlusClient connection.
        // Scopes indicate the information about the user your application will be able to access.
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.inject(this, v);

        if (supportsGooglePlayServices()) {
            updateUserInfo();
            // Set a listener to connect the user when the G+ button is clicked.
            mPlusSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mGoogleApiClient.isConnected()) {
                        // Show the dialog as we are now signing in.
                        showProgress(true);
                    }
                }
            });
        } else {
            // Don't offer G+ sign in if the app's version is too low to support Google Play
            // Services.
            mPlusSignInButton.setVisibility(View.GONE);
        }
        mFacebookLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Session session = Session.getActiveSession();
                if (!session.isOpened() && !session.isClosed()) {
                    session.openForRead(new Session.OpenRequest(getActivity())
                            .setPermissions("public_profile")
                            .setCallback(LoginFragment.this));
                } else {
                    Session.openActiveSession(getActivity(), true, LoginFragment.this);
                }
            }
        });
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    /**
     * Successfully connected (called by PlusClient)
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        showProgress(false);
        updateUserInfo();
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
        if (!mIntentInProgress && result.hasResolution()) {
            //try {
            mIntentInProgress = true;
                /*startIntentSenderForResult(result.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);*/
            /*} catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }*/
        }
    }

    /**
     * An earlier connection failed, and we're now receiving the result of the resolution attempt
     * by PlusClient.
     *
     * @see #onConnectionFailed(ConnectionResult)
     */
    @Override
    public void onConnectionSuspended(int i) {
        showProgress(false);
        LogHelper.logError("Connection suspended, reason " + i);
    }


    @Override
    public void call(Session session, SessionState sessionState, Exception e) {
        updateUserInfo();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void updateUserInfo() {
        Session session = Session.getActiveSession();
        if (session != null) {
            LogHelper.logInfo("Session not null getting user info");
            // Request user data and show the results
            Request.newMeRequest(session, new Request.GraphUserCallback() {

                @Override
                public void onCompleted(GraphUser user, Response response) {
                    LogHelper.logInfo("Complete!");
                    if (user != null) {
                        LogHelper.logInfo("User not null omg, " + user.getFirstName());
                        mUserInfoView.setVisibility(View.VISIBLE);
                        mPlusSignInButton.setVisibility(View.GONE);
                        // Display the parsed user info
                        mUserInfoName.setText(user.getFirstName() + " " + user.getLastName());
                        mUserInfoGender.setText((String) user.asMap().get("gender"));
                        mUserInfoId.setText(user.getId());
                    }
                }
            }).executeAsync();
        } else if (mGoogleApiClient.isConnected()) {
            mUserInfoView.setVisibility(View.VISIBLE);
            mPlusSignInButton.setVisibility(View.GONE);
            Person loggedInUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            mUserInfoName.setText(loggedInUser.getDisplayName());
            mUserInfoGender.setText(loggedInUser.getGender() == Person.Gender.FEMALE ? "Female" : "Male");
            mUserInfoId.setText(loggedInUser.getId());
        } else {
            mUserInfoView.setVisibility(View.GONE);
            mPlusSignInButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Check if the device supports Google Play Services.  It's best
     * practice to check first rather than handling this as an error case.
     *
     * @return whether the device supports Google Play Services
     */
    private boolean supportsGooglePlayServices() {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()) ==
                ConnectionResult.SUCCESS;
    }

}



