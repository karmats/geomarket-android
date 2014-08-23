package com.geomarket.android.task;

import android.location.Location;
import android.os.AsyncTask;

import com.geomarket.android.api.Event;
import com.geomarket.android.api.service.GeoMarketServiceApi;
import com.geomarket.android.api.service.GeoMarketServiceApiBuilder;
import com.geomarket.android.util.LogHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;

/**
 * Task to fetch a list of events.
 */
public class FetchEventsTask extends AsyncTask<Location, Void, List<Event>> {

    // The map to update with the events
    private GoogleMap mMap;
    // Api
    private GeoMarketServiceApi api;

    public FetchEventsTask(GoogleMap map) {
        this.mMap = map;
        this.api = GeoMarketServiceApiBuilder.newInstance();
    }

    @Override
    protected void onPostExecute(List<Event> events) {
        for (Event e : events) {
            LogHelper.logInfo("Location: " + e.getLocation());
            if (e.getLocation() != null) {
                LogHelper.logInfo("Adding marker at " + e.getLocation().getLat() + ", " + e.getLocation().getLon());
                mMap.addMarker(new MarkerOptions().position(e.getLocation().toLatLng()).title(e.getCompanyName()));
            }
        }
    }

    @Override
    protected List<Event> doInBackground(Location... locations) {
        Location l = locations[0];
        List<Event> result = new ArrayList<Event>();
        try {
            result = api.getEventsForLocation(l.getLatitude(), l.getLongitude(), 200, "EN");
        } catch (RetrofitError e) {
            LogHelper.logException(e);
        }
        LogHelper.logInfo("Got list of " + result.size());
        return result;
    }
}
