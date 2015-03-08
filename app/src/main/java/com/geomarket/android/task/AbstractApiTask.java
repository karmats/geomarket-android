package com.geomarket.android.task;

import android.os.AsyncTask;

import com.geomarket.android.api.ApiResult;
import com.geomarket.android.api.service.GeoMarketServiceApi;
import com.geomarket.android.api.service.GeoMarketServiceApiBuilder;
import com.geomarket.android.util.LogHelper;

import retrofit.RetrofitError;

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

    /**
     * Task that subclasses needs to implement, that do the actual api call
     *
     * @return ApiResult
     * @throws retrofit.RetrofitError If something goes wrong
     */
    abstract ApiResult<R> fetchFromServer(P... params) throws RetrofitError;

    @Override
    protected ApiResult<R> doInBackground(P... params) {
        try {
            return fetchFromServer(params);
        } catch (RetrofitError e) {
            return handleException(e);
        }
    }

    @Override
    protected void onPostExecute(ApiResult<R> apiResult) {
        if (apiResult.getError() != null) {
            mCallback.onFailure(apiResult.getError());
        } else if (apiResult.getCode() < 200 || apiResult.getCode() >= 300) {
            mCallback.onFailure("Failed to fetch data. Error code " + apiResult.getCode());
        } else {
            mCallback.onSuccess(apiResult.getData());
        }
    }

    // Handling retrofit error, calling callback failure
    private ApiResult<R> handleException(RetrofitError e) {
        StringBuilder message = new StringBuilder("Oops, got an error.\n").append("Kind: ");
        switch (e.getKind()) {
            case CONVERSION:
                message.append("Conversation error");
                break;
            case HTTP:
                message.append("Http error");
                break;
            case NETWORK:
                message.append("Network error");
                break;
            default:
                message.append("Unexpected error");
                break;
        }
        message.append("\n").append("Expected type: ").append(e.getSuccessType());
        // Log it
        LogHelper.logException(e);
        ApiResult<R> result = new ApiResult<>();
        result.setError(message.toString());
        return result;
    }

    /**
     * Callback for success and failure.
     *
     * @param <R> The expected return type
     */
    public interface ApiCallback<R> {
        void onSuccess(R result);

        void onFailure(String error);
    }
}
