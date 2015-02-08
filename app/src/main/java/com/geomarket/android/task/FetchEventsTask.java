package com.geomarket.android.task;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;

import com.geomarket.android.api.ApiResult;
import com.geomarket.android.api.Event;
import com.geomarket.android.api.Language;

import java.util.List;

import retrofit.RetrofitError;

/**
 * Task to fetch a list of events.
 */
public class FetchEventsTask extends AbstractApiTask<Location, List<Event>> {

    private final Context mContext;

    public FetchEventsTask(Context context, ApiCallback<List<Event>> callback) {
        super(callback);
        this.mContext = context;
    }

    @Override
    ApiResult<List<Event>> fetchFromServer(Location... locations) throws RetrofitError {
        Location loc = locations[0];
        String languageId = PreferenceManager.getDefaultSharedPreferences(mContext).getString(Language.PREF_LANGUAGE_ID, "");
        return mApi.getEventsForLocation(loc.getLatitude(), loc.getLongitude(), 200, languageId);
    }

}
