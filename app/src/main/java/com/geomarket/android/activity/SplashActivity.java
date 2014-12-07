package com.geomarket.android.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;

import com.geomarket.android.R;
import com.geomarket.android.api.Event;
import com.geomarket.android.task.FetchEventsTask;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends Activity implements FetchEventsTask.OnEventsFetchedCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Fetch events near user
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        new FetchEventsTask(this).execute(loc);
    }

    @Override
    public void onEventsFetched(List<Event> events) {
        startViewEventsActivity(events);
    }

    @Override
    public void onEventsFetchedFailure(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        startViewEventsActivity(new ArrayList<Event>());
    }

    private void startViewEventsActivity(List<Event> events) {
        // Start the view events activity
        Intent intent = new Intent(this, ViewEventsActivity.class);
        intent.putParcelableArrayListExtra(ViewEventsActivity.EVENTS_EXTRA, new ArrayList<Parcelable>(events));
        startActivity(intent);
        // Finish this activity, so it won't come up on back-button press
        finish();
    }
}
