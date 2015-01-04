package com.geomarket.android.task;

import android.content.Context;
import android.preference.PreferenceManager;

import com.geomarket.android.api.ApiResult;
import com.geomarket.android.api.Category;
import com.geomarket.android.api.Language;
import com.geomarket.android.util.LogHelper;

import java.util.List;

import retrofit.RetrofitError;

/**
 * Task to fetch all categories
 */
public class FetchCategoriesTask extends AbstractApiTask<Void, List<Category>> {

    private final Context mContext;

    public FetchCategoriesTask(Context context, ApiCallback<List<Category>> callback) {
        super(callback);
        this.mContext = context;
    }

    @Override
    protected ApiResult<List<Category>> doInBackground(Void... params) {
        try {
            String languageId = PreferenceManager.getDefaultSharedPreferences(mContext).getString(Language.PREF_LANGUAGE_ID, "");
            return mApi.getCategoriesByLanguage(languageId);
        } catch (RetrofitError e) {
            LogHelper.logException(e);
        }
        return null;
    }

}
