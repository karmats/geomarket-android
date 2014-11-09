package com.geomarket.android.task;

import android.app.Activity;
import android.os.AsyncTask;

import com.geomarket.android.api.AuthenticatedUser;
import com.geomarket.android.api.service.GeoMarketServiceApi;
import com.geomarket.android.api.service.GeoMarketServiceApiBuilder;

/**
 * Authenticates to Dibbler
 *
 * The token we got from facebook or google must be in the parameters.
 * This task returns a AuthenticatedUser
 */
public class AuthenticateToDibblerTask extends AsyncTask<String, Void, AuthenticatedUser> {

    public enum AuthenticateService {
        GOOGLE("google"), FACEBOOK("facebook")

        private String mPostParam;

        private AuthenticateService(String postParam) {
            mPostParam = postParam;
        }
    }

    private Activity mActivity;
    private AuthenticateService mAuthenticateService;
    // Api
    private GeoMarketServiceApi api;

    public AuthenticateToDibblerTask(Activity caller, AuthenticateService service) {
        mActivity = caller;
        mAuthenticateService = service;
        this.api = GeoMarketServiceApiBuilder.newInstance();
    }

    @Override
    protected AuthenticatedUser doInBackground(String... token) {
        AuthenticatedUser result = api.authenticate(mAuthenticateService.mPostParam, token[0]);
        return result;
    }
}
