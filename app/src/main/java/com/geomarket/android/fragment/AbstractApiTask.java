package com.geomarket.android.fragment;

import android.os.AsyncTask;

import com.geomarket.android.api.ApiResult;
import com.geomarket.android.api.service.GeoMarketServiceApi;
import com.geomarket.android.api.service.GeoMarketServiceApiBuilder;

/**
 * Abstract class for geomarket api calls
 */
public abstract class AbstractApiTask<P, R> extends AsyncTask<P, Void, ApiResult<R>> {

    // Api
    public final GeoMarketServiceApi mApi;
    // Callback on success and failure
    final ApiCallback<R> mCallback;

    protected AbstractApiTask(ApiCallback<R> callback) {
        this.mApi = GeoMarketServiceApiBuilder.newInstance();
        this.mCallback = callback;
    }

    @Override
    protected void onPostExecute(ApiResult<R> apiResult) {
        if (apiResult == null) {
            mCallback.onFailure("Got null result from server");
        } else if (apiResult.getCode() < 200 || apiResult.getCode() >= 300) {
            mCallback.onFailure("Failed to fetch data. Error code " + apiResult.getCode());
        } else {
            mCallback.onSuccess(apiResult.getData());
        }
    }

    public interface ApiCallback<R> {
        void onSuccess(R result);

        void onFailure(String error);
    }
}
