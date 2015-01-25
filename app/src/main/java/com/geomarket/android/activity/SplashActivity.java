package com.geomarket.android.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.geomarket.android.R;
import com.geomarket.android.api.Category;
import com.geomarket.android.api.Event;
import com.geomarket.android.api.Language;
import com.geomarket.android.task.AbstractApiTask;
import com.geomarket.android.task.FetchCategoriesTask;
import com.geomarket.android.task.FetchEventsTask;
import com.geomarket.android.task.FetchLanguages;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SplashActivity extends Activity {
    private static final String DEFAULT_LANGUAGE = "SE";

    private ArrayList<Event> mEvents;
    private ArrayList<Category> mCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Get the language id we use in all api calls
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String languageId = preferences.getString(Language.PREF_LANGUAGE_ID, "");
        if (!languageId.isEmpty()) {
            fetchCategoriesAndEvents(languageId);
        } else {
            // Fetch available languages
            new FetchLanguages(new AbstractApiTask.ApiCallback<List<Language>>() {
                @Override
                public void onSuccess(List<Language> result) {
                    String languageId = null;
                    String defaultLang = Locale.getDefault().getLanguage();
                    for (Language lang : result) {
                        if ((DEFAULT_LANGUAGE.equalsIgnoreCase(lang.getShortName()) && languageId == null)
                                || defaultLang.equalsIgnoreCase(lang.getShortName())) {
                            languageId = lang.getId();
                        }
                    }
                    // Store language id in preferences
                    preferences.edit().putString(Language.PREF_LANGUAGE_ID, languageId).commit();
                    fetchCategoriesAndEvents(languageId);
                }

                @Override
                public void onFailure(String error) {
                    // Couldn't get language, stop the application
                    Toast.makeText(SplashActivity.this, error, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }).execute();
        }

    }

    private void fetchCategoriesAndEvents(String langId) {
        // Fetch events near user
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        new FetchEventsTask(this, new AbstractApiTask.ApiCallback<List<Event>>() {
            @Override
            public void onSuccess(List<Event> result) {
                // TODO Groom this list, make some of it as one event
                mEvents = new ArrayList<>(result.subList(0, result.size() > 100 ? 100 : result.size()));
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
        new FetchCategoriesTask(this, new AbstractApiTask.ApiCallback<List<Category>>() {
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
