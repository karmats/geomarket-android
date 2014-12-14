package com.geomarket.android.task;

import android.location.Location;

import com.geomarket.android.api.ApiResult;
import com.geomarket.android.api.Event;
import com.geomarket.android.util.LogHelper;

import java.util.List;

import retrofit.RetrofitError;

/**
 * Task to fetch a list of events.
 */
public class FetchEventsTask extends AbstractApiTask<Location, List<Event>> {

    public FetchEventsTask(ApiCallback<List<Event>> callback) {
        super(callback);
    }

    @Override
    protected ApiResult<List<Event>> doInBackground(Location... locations) {
        Location loc = locations[0];
        try {
            return mApi.getEventsForLocation(loc.getLatitude(), loc.getLongitude(), 200, "EN");
        } catch (RetrofitError e) {
            LogHelper.logException(e);
        }
        return null;
    }
}
