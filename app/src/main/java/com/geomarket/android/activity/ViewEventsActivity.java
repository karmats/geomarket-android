package com.geomarket.android.activity;

import android.app.ActionBar;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.geomarket.android.R;
import com.geomarket.android.api.Event;
import com.geomarket.android.fragment.ViewListEventsFragment;
import com.geomarket.android.fragment.ViewMapEventsFragment;
import com.geomarket.android.util.LogHelper;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Locale;

public class ViewEventsActivity extends FragmentActivity implements ActionBar.TabListener, ViewListEventsFragment.OnEventClickListener {

    public static final String EVENTS_EXTRA = "events_extra";

    public static final String SAVED_STATE_ACTION_BAR_HIDDEN = "saved_state_action_bar_hidden";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    /**
     * The {@link SlidingUpPanelLayout} that will show details about an event.
     */
    SlidingUpPanelLayout mDetailsPanelLayout;

    // Latest known location
    private Location mLatestLocation;
    // The events
    private ArrayList<Event> mEvents = new ArrayList<Event>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);

        // Get the events from extra
        mEvents = getIntent().getParcelableArrayListExtra(EVENTS_EXTRA);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // Sliding up panel
        mDetailsPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mDetailsPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelExpanded(View panel) {
                // Hide tabs when expaned so the whole view will be shown
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            }

            @Override
            public void onPanelCollapsed(View panel) {
                // View tabs when collapsed
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            }

            @Override
            public void onPanelAnchored(View panel) {
            }

            @Override
            public void onPanelHidden(View view) {
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
    }

    // Clicking an event in list view
    @Override
    public void onEventClick(String id) {
        LogHelper.logInfo("Event " + id + " clicked");
    }

    @Override
    public void onBackPressed() {
        if (mDetailsPanelLayout.isPanelExpanded()) {
            mDetailsPanelLayout.collapsePanel();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Shows information about an event.
     *
     * @param event The event to view more info about.
     */
    public void viewEvent(Event event) {
        mDetailsPanelLayout.showPanel();
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
                    return ViewMapEventsFragment.newInstance(mEvents, new Event.Location(mLatestLocation.getLatitude(),
                            mLatestLocation.getLongitude()));
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
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_fragment_view_map_events).toUpperCase(l);
                case 1:
                    return getString(R.string.title_fragment_view_list_events).toUpperCase(l);
            }
            return null;
        }
    }
}
