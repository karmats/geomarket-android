package com.geomarket.android.task;

import android.location.Location;
import android.os.AsyncTask;

import com.geomarket.android.api.Event;
import com.geomarket.android.api.service.GeoMarketServiceApi;
import com.geomarket.android.api.service.GeoMarketServiceApiBuilder;
import com.geomarket.android.util.LogHelper;

import java.util.List;

import retrofit.RestAdapter;

/**
 * Task to fetch a list of events.
 */
public class FetchEventsTask extends AsyncTask<Location, Void, List<Event>> {

    // Api
    private GeoMarketServiceApi api = GeoMarketServiceApiBuilder.newInstance();

    @Override
    protected List<Event> doInBackground(Location... locations) {
        Location l = locations[0];
        List<Event> result = api.getEventsForLocation(l.getLatitude(), l.getLongitude(), "EN");
        LogHelper.logInfo("Got list of " + result.size());
        return result;
    }
}
