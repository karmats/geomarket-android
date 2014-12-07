package com.geomarket.android.task;

import android.location.Location;
import android.os.AsyncTask;

import com.geomarket.android.api.ApiResult;
import com.geomarket.android.api.Event;
import com.geomarket.android.api.service.GeoMarketServiceApi;
import com.geomarket.android.api.service.GeoMarketServiceApiBuilder;
import com.geomarket.android.util.LogHelper;

import java.util.List;

import retrofit.RetrofitError;

/**
 * Task to fetch a list of events.
 */
public class FetchEventsTask extends AsyncTask<Location, Void, ApiResult<List<Event>>> {

    // Calling activity
    private OnEventsFetchedCallback mCallback;
    // Api
    private GeoMarketServiceApi api;

    public FetchEventsTask(OnEventsFetchedCallback callback) {
        this.mCallback = callback;
        this.api = GeoMarketServiceApiBuilder.newInstance();
    }

    @Override
    protected ApiResult<List<Event>> doInBackground(Location... locations) {
        Location loc = locations[0];
        try {
            ApiResult<List<Event>> res = api.getEventsForLocation(loc.getLatitude(), loc.getLongitude(), 200, "EN");
            return res;
        } catch (RetrofitError e) {
            LogHelper.logException(e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(ApiResult<List<Event>> listApiResult) {
        // Ok
        if (listApiResult.getCode() == 200) {
            mCallback.onEventsFetched(listApiResult.getData());
            LogHelper.logInfo("Got list of " + listApiResult.getData().size());
        } else {
            mCallback.onEventsFetchedFailure("Failed to fetch events error code " + listApiResult.getCode());
        }
    }

    /**
     * Callbacks for this task
     */
    public interface OnEventsFetchedCallback {
        /**
         * @param events Events result
         */
        void onEventsFetched(List<Event> events);

        void onEventsFetchedFailure(String error);
    }
}
