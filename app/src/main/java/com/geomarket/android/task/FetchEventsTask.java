package com.geomarket.android.task;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Parcelable;

import com.geomarket.android.activity.ViewEventsActivity;
import com.geomarket.android.api.Event;
import com.geomarket.android.api.service.GeoMarketServiceApi;
import com.geomarket.android.api.service.GeoMarketServiceApiBuilder;
import com.geomarket.android.util.LogHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;

/**
 * Task to fetch a list of events.
 */
public class FetchEventsTask extends AsyncTask<Location, Void, List<Event>> {

    // Calling activity
    // TODO Change this
    private Activity mContext;
    // Api
    private GeoMarketServiceApi api;

    public FetchEventsTask(Activity context) {
        this.mContext = context;
        this.api = GeoMarketServiceApiBuilder.newInstance();
    }

    @Override
    protected void onPostExecute(List<Event> events) {
        Intent viewEventsActivity = new Intent(mContext.getApplicationContext(), ViewEventsActivity.class);
        viewEventsActivity.putParcelableArrayListExtra(ViewEventsActivity.EVENTS_EXTRA, new ArrayList<Parcelable>(events));
        mContext.startActivity(viewEventsActivity);
        mContext.finish();
    }

    @Override
    protected List<Event> doInBackground(Location... locations) {
        Location loc = locations[0];
        List<Event> result = new ArrayList<Event>();
        try {
            result = api.getEventsForLocation(loc.getLatitude(), loc.getLongitude(), 200, "EN");
        } catch (RetrofitError e) {
            LogHelper.logException(e);
        }
        LogHelper.logInfo("Got list of " + result.size());
        return result;
    }
}
