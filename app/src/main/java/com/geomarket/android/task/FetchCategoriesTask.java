package com.geomarket.android.task;

import android.os.AsyncTask;

import com.geomarket.android.api.ApiResult;
import com.geomarket.android.api.Category;
import com.geomarket.android.api.service.GeoMarketServiceApi;
import com.geomarket.android.api.service.GeoMarketServiceApiBuilder;
import com.geomarket.android.util.LogHelper;

import java.util.List;

import retrofit.RetrofitError;

/**
 * Task to fetch all categories
 */
public class FetchCategoriesTask extends AsyncTask<Void, Void, ApiResult<List<Category>>> {

    // Calling activity
    private OnCategoriesFetchedCallback mCallback;
    // Api
    private GeoMarketServiceApi mApi;

    public FetchCategoriesTask(OnCategoriesFetchedCallback callback) {
        this.mCallback = callback;
        this.mApi = GeoMarketServiceApiBuilder.newInstance();
    }

    @Override
    protected ApiResult<List<Category>> doInBackground(Void... params) {
        try {
            return mApi.getCategories();
        } catch (RetrofitError e) {
            LogHelper.logException(e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(ApiResult<List<Category>> listApiResult) {
        if (listApiResult == null) {
            mCallback.onCategoriesFetchedFailure("Failed to fetch categories");
        } else if (listApiResult.getCode() < 200 || listApiResult.getCode() >= 300) {
            mCallback.onCategoriesFetchedFailure("Failed to fetch categories. error code " + listApiResult.getCode());
        } else {
            LogHelper.logInfo("Got list of " + listApiResult.getData().size());
            mCallback.onCategoriesFetched(listApiResult.getData());
        }
    }

    public interface OnCategoriesFetchedCallback {
        /**
         * @param categories The fetched categories
         */
        void onCategoriesFetched(List<Category> categories);

        /**
         * @param error Error message
         */
        void onCategoriesFetchedFailure(String error);
    }
}
