package com.geomarket.android.task;

import com.geomarket.android.api.ApiResult;
import com.geomarket.android.api.User;

import retrofit.RetrofitError;

/**
 * Creates a new dibbler user
 * <p/>
 * User can be created via facebook or google
 */
public class CreateNewUserTask extends AbstractApiTask<Void, User> {

    private User user;

    /**
     * @param callback
     * @param user     The user to create
     */
    public CreateNewUserTask(ApiCallback<User> callback, User user) {
        super(callback);
        this.user = user;
    }

    @Override
    ApiResult<User> fetchFromServer(Void... params) throws RetrofitError {
        return mApi.createNewUser(user);
    }

}
