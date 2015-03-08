package com.geomarket.android.activity;

import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.geomarket.android.R;
import com.geomarket.android.api.Category;
import com.geomarket.android.api.Event;
import com.geomarket.android.api.service.GeoMarketServiceApiBuilder;
import com.geomarket.android.fragment.LoginFragment;
import com.geomarket.android.fragment.MapEventsFragment;
import com.geomarket.android.fragment.ViewEventDetailsFragment;
import com.geomarket.android.fragment.ViewEventsFragment;
import com.geomarket.android.fragment.ViewListEventsFragment;
import com.geomarket.android.util.LogHelper;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends ActionBarActivity implements ViewListEventsFragment.OnListEventClickListener, MapEventsFragment.OnMapEventClickListener, ViewEventsFragment.OnViewEventsListener {

    // Extras from splash activity
    public static final String EVENTS_EXTRA = "events_extra";
    public static final String CATEGORIES_EXTRA = "categories_extra";
    public static final String LOCATION_EXTRA = "location_extra";

    // Tag for the view events fragment
    public static final String VIEW_EVENTS_TAG = "view_events_tag";

    /**
     * The {@link SlidingUpPanelLayout} that will show details about an event.
     */
    @InjectView(R.id.sliding_layout)
    SlidingUpPanelLayout mDetailsPanelLayout;

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

        // Get the events from extra
        mEvents = getIntent().getParcelableArrayListExtra(EVENTS_EXTRA);
        mCategories = getIntent().getParcelableArrayListExtra(CATEGORIES_EXTRA);
        mLatestLocation = getIntent().getParcelableExtra(LOCATION_EXTRA);

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
                ViewEventsFragment viewEventsFragment = (ViewEventsFragment) getFragmentManager().findFragmentByTag(VIEW_EVENTS_TAG);
                viewEventsFragment.onHideEventDetail();
            }

            @Override
            public void onPanelExpanded(View view) {
                ViewEventsFragment viewEventsFragment = (ViewEventsFragment) getFragmentManager().findFragmentByTag(VIEW_EVENTS_TAG);
                viewEventsFragment.onViewEventDetail();
            }

            @Override
            public void onPanelAnchored(View view) {
            }

            @Override
            public void onPanelHidden(View view) {
            }
        });

        // Start view events fragment
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, ViewEventsFragment.newInstance(mEvents, mCategories,
                new Event.Location(mLatestLocation.getLatitude(), mLatestLocation.getLongitude())), VIEW_EVENTS_TAG).addToBackStack(null).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_events, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.menu_search));
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
            LogHelper.logInfo("Action login clicked");
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, LoginFragment.newInstance())
                    .addToBackStack(null).commit();
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

    // From the ViewEventsFragment
    @Override
    public void viewNextEvent() {
        int idx = mEvents.indexOf(mCurrentEvent);
        viewEvent(mEvents.get((idx + 1) >= mEvents.size() ? 0 : idx + 1));
    }

    @Override
    public void viewPreviousEvent() {
        int idx = mEvents.indexOf(mCurrentEvent);
        viewEvent(mEvents.get((idx - 1) <= 0 ? mEvents.size() - 1 : idx - 1));
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

}
