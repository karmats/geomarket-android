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
    private GeoMarketServiceApi mApi;

    public FetchEventsTask(OnEventsFetchedCallback callback) {
        this.mCallback = callback;
        this.mApi = GeoMarketServiceApiBuilder.newInstance();
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

    @Override
    protected void onPostExecute(ApiResult<List<Event>> listApiResult) {
        if (listApiResult == null) {
            mCallback.onEventsFetchedFailure("Failed to fetch events");
        } else if (listApiResult.getCode() < 200 || listApiResult.getCode() >= 300) {
            mCallback.onEventsFetchedFailure("Failed to fetch events. Error code " + listApiResult.getCode());
        } else {
            LogHelper.logInfo("Got category list of " + listApiResult.getData().size());
            mCallback.onEventsFetched(listApiResult.getData());
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

        /**
         * @param error Error message
         */
        void onEventsFetchedFailure(String error);
    }
}
