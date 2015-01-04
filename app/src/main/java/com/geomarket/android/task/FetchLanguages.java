package com.geomarket.android.task;

import com.geomarket.android.api.ApiResult;
import com.geomarket.android.api.Language;
import com.geomarket.android.util.LogHelper;

import java.util.List;

import retrofit.RetrofitError;

/**
 * Fetches all supported languages
 */
public class FetchLanguages extends AbstractApiTask<Void, List<Language>> {

    public FetchLanguages(ApiCallback<List<Language>> callback) {
        super(callback);
    }

    @Override
    protected ApiResult<List<Language>> doInBackground(Void... params) {
        try {
            return mApi.getLanguages();
        } catch (RetrofitError e) {
            LogHelper.logException(e);
        }
        return null;
    }
}
