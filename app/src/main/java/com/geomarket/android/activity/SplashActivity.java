package com.geomarket.android.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;

import com.geomarket.android.R;
import com.geomarket.android.api.Category;
import com.geomarket.android.api.Event;
import com.geomarket.android.api.Language;
import com.geomarket.android.task.AbstractApiTask;
import com.geomarket.android.task.FetchCategoriesTask;
import com.geomarket.android.task.FetchEventsTask;
import com.geomarket.android.task.FetchLanguages;
import com.geomarket.android.util.LogHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SplashActivity extends Activity {
    private static final String DEFAULT_LANGUAGE = "SE";
    private static final String DEFAULT_COUNTRY = "SWE";

    private ArrayList<Event> mEvents;
    private ArrayList<Category> mCategories;
    private Location mLocation;

    // Views
    @InjectView(R.id.loading_text)
    TextView mInitText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // Inject the views
        ButterKnife.inject(this);

        mInitText.setText(getString(R.string.init_initializing));
        // Get the language id we use in all api calls
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // TODO Uncomment when having something stable
        final String languageId = "";//preferences.getString(Language.PREF_LANGUAGE_ID, "");
        if (!languageId.isEmpty()) {
            fetchCategoriesAndEvents();
        } else {
            // Fetch available languages
            new FetchLanguages(new AbstractApiTask.ApiCallback<List<Language>>() {
                @Override
                public void onSuccess(List<Language> result) {
                    String languageId = null;
                    String defaultLang = Locale.getDefault().getLanguage();
                    for (Language lang : result) {
                        if (((DEFAULT_LANGUAGE.equalsIgnoreCase(lang.getShortName()) || DEFAULT_COUNTRY.equalsIgnoreCase(lang.getShortName())) && languageId == null)
                                || defaultLang.equalsIgnoreCase(lang.getShortName())) {
                            languageId = lang.getId();
                        }
                    }
                    // Store language id in preferences
                    preferences.edit().putString(Language.PREF_LANGUAGE_ID, languageId).commit();
                    fetchCategoriesAndEvents();
                }

                @Override
                public void onFailure(String error) {
                    // Couldn't get language, stop the application
                    Toast.makeText(SplashActivity.this, error, Toast.LENGTH_LONG).show();
                    //finish();
                    fetchCategoriesAndEvents();
                }
            }).execute();
        }

    }

    private void fetchCategoriesAndEvents() {
        // Fetch events near user
        mInitText.setText(getString(R.string.init_fetch_position));
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (mLocation != null) {
            buildFetchEventsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mLocation);
        } else {
            // No last known location found, request for it
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    mLocation = location;
                    buildFetchEventsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mLocation);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    LogHelper.logInfo("Got " + status + " from provider " + provider);
                }

                @Override
                public void onProviderEnabled(String provider) {
                    LogHelper.logInfo("Provider " + provider + " enabled");
                }

                @Override
                public void onProviderDisabled(String provider) {
                    LogHelper.logInfo("Provider " + provider + " disabled");
                }
            }, null);
        }

        // Fetch the categories
        new FetchCategoriesTask(this, new AbstractApiTask.ApiCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> result) {
                mCategories = new ArrayList<>(result);
                startViewEventsActivity();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(SplashActivity.this, error, Toast.LENGTH_LONG).show();
                mCategories = new ArrayList<>();
                startViewEventsActivity();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private FetchEventsTask buildFetchEventsTask() {
        mInitText.setText(getString(R.string.init_fetch_events));
        return new FetchEventsTask(this, new AbstractApiTask.ApiCallback<List<Event>>() {
            @Override
            public void onSuccess(List<Event> result) {
                // TODO Groom this list, make some of it as one event
                mEvents = new ArrayList<>(result.subList(0, result.size() > 100 ? 100 : result.size()));
                Event e = new Event();
                e.setLocation(new Event.Location(57.708870, 11.974560));
                e.setExpires(123L);
                Event.Company c = new Event.Company();
                c.setCity("Gbg");
                c.setName("Bolaget");
                c.setStreet("Giefgatan 22");
                c.setWww("www.systembolaget.se");
                e.setCompany(c);
                Event.Text t = new Event.Text();
                t.setBody("Hej hej");
                t.setHeading("Ett event");
                e.setText(t);
                mEvents.add(e);
                startViewEventsActivity();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(SplashActivity.this, error, Toast.LENGTH_LONG).show();
                mEvents = new ArrayList<>();
                startViewEventsActivity();
            }
        });
    }

    private void startViewEventsActivity() {
        // Only Start the activity if both categories and categories have been fetched
        if (mEvents != null && mCategories != null) {
            // Start the view events activity
            Intent intent = new Intent(this, MainActivity.class);
            intent.putParcelableArrayListExtra(MainActivity.EVENTS_EXTRA, mEvents);
            intent.putParcelableArrayListExtra(MainActivity.CATEGORIES_EXTRA, mCategories);
            intent.putExtra(MainActivity.LOCATION_EXTRA, mLocation);
            startActivity(intent);
            // Finish this activity, so it won't come up on back-button press
            finish();
        }
    }
}
