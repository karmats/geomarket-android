package com.geomarket.android.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.geomarket.android.R;
import com.geomarket.android.api.Category;
import com.geomarket.android.api.Event;
import com.geomarket.android.api.service.GeoMarketServiceApiBuilder;
import com.geomarket.android.fragment.MapEventsFragment;
import com.geomarket.android.fragment.ViewEventDetailsFragment;
import com.geomarket.android.fragment.ViewEventsFragment;
import com.geomarket.android.fragment.ViewListEventsFragment;
import com.geomarket.android.task.DownloadImageTask;
import com.geomarket.android.util.LogHelper;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.NumberFormat;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ViewEventsActivity extends ActionBarActivity implements ViewListEventsFragment.OnListEventClickListener, MapEventsFragment.OnMapEventClickListener, ViewEventsFragment.OnLayoutChangeListener {

    public static final String EVENTS_EXTRA = "events_extra";
    public static final String CATEGORIES_EXTRA = "categories_extra";

    /**
     * The {@link SlidingUpPanelLayout} that will show details about an event.
     */
    @InjectView(R.id.sliding_layout)
    SlidingUpPanelLayout mDetailsPanelLayout;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    @InjectView(R.id.pager)
    ViewPager mViewPager;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    // Views
    @InjectView(R.id.main_toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.view_event_detail_title)
    TextView mEventTitleTextView;
    @InjectView(R.id.view_event_company_name)
    TextView mEventCompanyName;
    @InjectView(R.id.view_event_distance)
    TextView mEventDistance;
    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @InjectView(R.id.left_drawer)
    ListView mDrawerList;
    @InjectView(R.id.view_event_detail_thumb)
    ImageView mEventThumb;

    private ArrayList<Event> mEvents;
    private ArrayList<Category> mCategories;

    private ActionBarDrawerToggle mDrawerToggle;
    private Location mLatestLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);

        // Inject the views
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Get the events from extra
        mEvents = getIntent().getParcelableArrayListExtra(EVENTS_EXTRA);
        mCategories = getIntent().getParcelableArrayListExtra(CATEGORIES_EXTRA);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, mCategories));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogHelper.logInfo(position + " clicked");
            }
        });

        // User location
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLatestLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_events, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();
        if (id == R.id.action_login) {
            startActivity(new Intent(ViewEventsActivity.this, LoginActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (mDetailsPanelLayout.isPanelExpanded()) {
            mDetailsPanelLayout.collapsePanel();
        } else if (mDrawerLayout.isDrawerOpen(Gravity.START | Gravity.LEFT)) {
            mDrawerLayout.closeDrawers();
            return;
        } else {
            super.onBackPressed();
        }
    }

    // Layout has changed from map to list, or vice versa
    @Override
    public void onLayoutChange(ViewEventsFragment.ViewType type) {
        mDetailsPanelLayout.hidePanel();
    }

    // Clicking an event in list view
    @Override
    public void onListEventClick(Event event) {
        setupEvent(event);
        getFragmentManager().beginTransaction().replace(
                R.id.view_event_fragment, ViewEventDetailsFragment.newInstance(event)).commit();
        mDetailsPanelLayout.expandPanel();
    }

    // Clicking an event in map view
    @Override
    public void onMapEventClick(Event event) {
        setupEvent(event);
        getFragmentManager().beginTransaction().replace(
                R.id.view_event_fragment, ViewEventDetailsFragment.newInstance(event)).commit();
        mDetailsPanelLayout.showPanel();
    }

    // Clicking somewhere on the map
    @Override
    public void onMapClick() {
        mDetailsPanelLayout.hidePanel();
    }

    private void setupEvent(Event event) {
        new DownloadImageTask(this, GeoMarketServiceApiBuilder.HOST + event.getImageSmallUrl(), mEventThumb).execute();
        mEventTitleTextView.setText(event.getText().getHeading());
        mEventCompanyName.setText(event.getCompany().getName());
        Location companyLoc = new Location("me");
        companyLoc.setLatitude(event.getLocation().getLatitude());
        companyLoc.setLongitude(event.getLocation().getLongitude());
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        String distanceString = nf.format(mLatestLocation.distanceTo(companyLoc) / 1000);
        mEventDistance.setText(distanceString + " km");
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return MapEventsFragment.newInstance(mEvents, mCategories,
                            new Event.Location(mLatestLocation.getLatitude(), mLatestLocation.getLongitude()));

                case 1:
                    return ViewListEventsFragment.newInstance(mEvents);
            }
            LogHelper.logError("Something is terribly wrong, got tab position " + position);
            return null;
        }

        @Override
        public int getCount() {

            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.title_fragment_view_map_events).toUpperCase();
                case 1:
                    return getString(R.string.title_fragment_view_list_events).toUpperCase();
            }
            return null;
        }
    }
}
