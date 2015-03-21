package com.geomarket.android.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.geomarket.android.R;
import com.geomarket.android.activity.IMainActivity;
import com.geomarket.android.api.User;
import com.geomarket.android.util.LogHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * A login screen that offers login via Google+ sign in.
 * <p/>
 */
public class LoginFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, Session.StatusCallback {

    // Fragment tag name
    public static final String TAG_NAME = "login_fragment_tag";
    // A magic number we will use to know that our sign-in error resolution activity has completed
    public static final int RC_SIGN_IN = 49404;

    // Client used to interact with Google APIs.
    private GoogleApiClient mGoogleApiClient;

    /* Track whether the sign-in button has been clicked so that we know to resolve
     * all issues preventing sign-in without waiting.
     */
    private boolean mSignInClicked;

    private boolean mIntentInProgress;

    /* Store the connection result from onConnectionFailed callbacks so that we can
     * resolve them when the user clicks sign-in.
     */
    private ConnectionResult mConnectionResult;

    // Needed for facebook login
    private UiLifecycleHelper uiHelper;

    // UI references.
    @InjectView(R.id.login_progress)
    View mProgressView;
    @InjectView((R.id.plus_sign_in_button))
    SignInButton mPlusSignInButton;
    @InjectView(R.id.plus_sign_out_button)
    Button mPlusSignOutButton;
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
    @InjectView(R.id.user_info_email)
    TextView mUserInfoEmail;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Facebook UI lifecycle helper
        uiHelper = new UiLifecycleHelper(getActivity(), this);
        uiHelper.onCreate(savedInstanceState);
        // Initialize the PlusClient connection.
        // Scopes indicate the information about the user your application will be able to access.
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        ButterKnife.inject(this, v);

        mFacebookLoginButton.setFragment(this);
        mFacebookLoginButton.setReadPermissions("public_profile", "email");
        mFacebookLoginButton.setSessionStatusCallback(this);

        if (supportsGooglePlayServices()) {
            // Set a listener to connect the user when the G+ button is clicked.
            mPlusSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mGoogleApiClient.isConnected()) {
                        // Show the dialog as we are now signing in.
                        showProgress(true);
                        mSignInClicked = true;
                        resolveSignInError();
                    }
                }
            });
        } else {
            // Don't offer G+ sign in if the app's version is too low to support Google Play
            // Services.
            mPlusSignInButton.setVisibility(View.GONE);
        }
        retrieveUserInfo();
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
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @OnClick(R.id.plus_sign_out_button)
    public void onPlusSignOut() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
        uiHelper.onActivityResult(requestCode, responseCode, intent);
    }

    /**
     * Successfully connected (called by PlusClient)
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        showProgress(false);
        retrieveUserInfo();
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


    // Callback for facebook button
    @Override
    public void call(Session session, SessionState sessionState, Exception e) {
        if (sessionState.isOpened()) {
            retrieveUserInfo();
        }
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

    /**
     * Connection failed for some reason (called by PlusClient)
     * Try and resolve the result.  Failure here is usually not an indication of a serious error,
     * just that the user's input is needed.
     *
     * @see #onActivityResult(int, int, Intent)
     */
    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress) {
            // Store the ConnectionResult so that we can use it later when the user clicks
            // 'sign-in'.
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    // Try to resolve sign in error when signing up via google+
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                getActivity().startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    // Retrieves user information from google or facebook
    private void retrieveUserInfo() {
        Session session = Session.getActiveSession();
        if (session != null && session.getState().isOpened()) {
            // Request user data and show the results
            Request.newMeRequest(session, new Request.GraphUserCallback() {

                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        createUser(User.fromFacebookUser(user));
                    }
                }
            }).executeAsync();
        } else if (mGoogleApiClient.isConnected()) {
            mUserInfoView.setVisibility(View.VISIBLE);
            mPlusSignInButton.setVisibility(View.GONE);
            mPlusSignOutButton.setVisibility(View.VISIBLE);

            Person gUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
            createUser(User.fromGoogleUser(gUser, email));
        } else {
            mUserInfoView.setVisibility(View.GONE);
            mPlusSignInButton.setVisibility(View.VISIBLE);
        }
    }

    private void createUser(User user) {
        mUserInfoView.setVisibility(View.VISIBLE);
        // TODO Execute CreateNewUserTask
        mUserInfoEmail.setText(user.getEmail());
        mUserInfoName.setText(user.getDisplayName());
        mUserInfoGender.setText(user.getGender());
        mUserInfoId.setText(user.getFacebookId() != null ? user.getFacebookId() : user.getGoogleId());
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