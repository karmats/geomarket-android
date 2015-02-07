package com.geomarket.android.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.geomarket.android.util.LogHelper;
import com.geomarket.android.view.SlidingTabLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

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
     * The {@link SlidingTabLayout} for tab indication.
     */
    @InjectView(R.id.sliding_tabs)
    SlidingTabLayout mSlidingTabLayout;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections.
     */
    ImagePagerAdapter mSectionsPagerAdapter;

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
    @InjectView(R.id.details_btn_view)
    LinearLayout mButtonView;
    @InjectView(R.id.details_next_btn)
    Button mNextButton;
    @InjectView(R.id.details_prev_btn)
    Button mPreviousButton;

    private ArrayList<Event> mEvents;
    private ArrayList<Category> mCategories;
    private Event mCurrentEvent;

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
        mSectionsPagerAdapter = new ImagePagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.light_orange));
        mSlidingTabLayout.setCustomTabView(R.layout.tab_item, 0, R.id.tab_item_img);
        mSlidingTabLayout.setViewPager(mViewPager);

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

        mDetailsPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
            }

            @Override
            public void onPanelCollapsed(View view) {
                mButtonView.setVisibility(View.GONE);
                mSlidingTabLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPanelExpanded(View view) {
                mButtonView.setVisibility(View.VISIBLE);
                mSlidingTabLayout.setVisibility(View.GONE);
            }

            @Override
            public void onPanelAnchored(View view) {
            }

            @Override
            public void onPanelHidden(View view) {
            }
        });

        // User location
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLatestLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }

    @OnClick(R.id.details_next_btn)
    public void onNextButtonClicked() {
        int idx = mEvents.indexOf(mCurrentEvent);
        viewEvent(mEvents.get((idx + 1) >= mEvents.size() ? 0 : idx + 1));
    }

    @OnClick(R.id.details_prev_btn)
    public void onPreviousButtonClicked() {
        int idx = mEvents.indexOf(mCurrentEvent);
        viewEvent(mEvents.get((idx - 1) <= 0 ? mEvents.size() - 1 : idx - 1));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_events, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        LogHelper.logInfo("View is " + searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                LogHelper.logInfo("Searching for " + s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                LogHelper.logInfo("Changing to " + s);
                return true;
            }
        });
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
        if (mDetailsPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            mDetailsPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
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
        mDetailsPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    // Clicking an event in list view
    @Override
    public void onListEventClick(Event event) {
        viewEvent(event);
        mDetailsPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    // Clicking an event in map view
    @Override
    public void onMapEventClick(Event event) {
        viewEvent(event);
        mDetailsPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    // Clicking somewhere on the map
    @Override
    public void onMapClick() {
        mDetailsPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    private void viewEvent(Event event) {
        mCurrentEvent = event;
        Picasso.with(this).load(GeoMarketServiceApiBuilder.HOST + event.getImageSmallUrl()).into(mEventThumb);
        mEventTitleTextView.setText(event.getText().getHeading());
        mEventCompanyName.setText(event.getCompany().getName());
        Location companyLoc = new Location("me");
        companyLoc.setLatitude(event.getLocation().getLatitude());
        companyLoc.setLongitude(event.getLocation().getLongitude());
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(1);
        String distanceString = nf.format(mLatestLocation.distanceTo(companyLoc) / 1000);
        mEventDistance.setText(distanceString + " km");
        getFragmentManager().beginTransaction().replace(
                R.id.view_event_fragment, ViewEventDetailsFragment.newInstance(event)).commit();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class ImagePagerAdapter extends FragmentPagerAdapter {

        public ImagePagerAdapter(FragmentManager fm) {
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

        public Drawable getPageDrawable(int position) {
            switch (position) {
                case 0:
                    return getDrawable(R.drawable.ic_action_icon_map);
                case 1:
                    return getDrawable(R.drawable.ic_action_icon_list);
            }
            return null;
        }
    }
}
