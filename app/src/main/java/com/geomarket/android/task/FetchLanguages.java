package com.geomarket.android.task;

import com.geomarket.android.api.ApiResult;
import com.geomarket.android.api.Language;

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
    ApiResult<List<Language>> fetchFromServer(Void... params) throws RetrofitError {
        return mApi.getLanguages();
    }

}
