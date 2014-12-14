package com.geomarket.android.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.geomarket.android.R;
import com.geomarket.android.api.Category;
import com.geomarket.android.api.Event;
import com.geomarket.android.task.AbstractApiTask;
import com.geomarket.android.task.FetchCategoriesTask;
import com.geomarket.android.task.FetchEventsTask;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends Activity {

    private ArrayList<Event> mEvents;
    private ArrayList<Category> mCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Fetch events near user
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        new FetchEventsTask(new AbstractApiTask.ApiCallback<List<Event>>() {
            @Override
            public void onSuccess(List<Event> result) {
                mEvents = new ArrayList<>(result);
                startViewEventsActivity();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(SplashActivity.this, error, Toast.LENGTH_SHORT).show();
                mEvents = new ArrayList<>();
                startViewEventsActivity();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, loc);

        // Fetch the categories
        new FetchCategoriesTask(new AbstractApiTask.ApiCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> result) {
                mCategories = new ArrayList<>(result);
                startViewEventsActivity();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(SplashActivity.this, error, Toast.LENGTH_SHORT).show();
                mCategories = new ArrayList<>();
                startViewEventsActivity();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void startViewEventsActivity() {
        // Only Start the activity if both categories and categories have been fetched
        if (mEvents != null && mCategories != null) {
            // Start the view events activity
            Intent intent = new Intent(this, ViewEventsActivity.class);
            intent.putParcelableArrayListExtra(ViewEventsActivity.EVENTS_EXTRA, mEvents);
            intent.putParcelableArrayListExtra(ViewEventsActivity.CATEGORIES_EXTRA, mCategories);
            startActivity(intent);
            // Finish this activity, so it won't come up on back-button press
            finish();
        }
    }
}
