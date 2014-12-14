package com.geomarket.android.task;

import com.geomarket.android.api.ApiResult;
import com.geomarket.android.api.Category;
import com.geomarket.android.fragment.AbstractApiTask;
import com.geomarket.android.util.LogHelper;

import java.util.List;

import retrofit.RetrofitError;

/**
 * Task to fetch all categories
 */
public class FetchCategoriesTask extends AbstractApiTask<Void, List<Category>> {

    public FetchCategoriesTask(ApiCallback<List<Category>> callback) {
        super(callback);
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

}
