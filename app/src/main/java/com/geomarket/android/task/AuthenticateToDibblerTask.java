package com.geomarket.android.task;

import com.geomarket.android.api.ApiResult;
import com.geomarket.android.api.AuthUser;
import com.geomarket.android.api.User;

import retrofit.RetrofitError;

/**
 * Authenticates to Dibbler
 * <p/>
 * Authentication via username/password alt. facebook or google id
 * This task returns a User object
 */
public class AuthenticateToDibblerTask extends AbstractApiTask<AuthUser, User> {

    private AuthUser authUser;

    /**
     * @param callback
     * @param authUser The user to authenticate
     */
    public AuthenticateToDibblerTask(ApiCallback<User> callback, AuthUser authUser) {
        super(callback);
        this.authUser = authUser;
    }

    @Override
    ApiResult<User> fetchFromServer(AuthUser... params) throws RetrofitError {
        return this.mApi.authenticateUser(params[0]);
    }
}
