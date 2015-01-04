package com.geomarket.android.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.geomarket.android.R;
import com.geomarket.android.api.Category;
import com.geomarket.android.api.Event;
import com.geomarket.android.fragment.MapEventsFragment;
import com.geomarket.android.fragment.ViewEventDetailsFragment;
import com.geomarket.android.fragment.ViewEventsFragment;
import com.geomarket.android.fragment.ViewListEventsFragment;
import com.geomarket.android.util.LogHelper;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

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

    // Views
    @InjectView(R.id.main_toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.view_event_detail_title)
    TextView mEventTitleTextView;
    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @InjectView(R.id.left_drawer)
    ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);

        // Inject the views
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Get the events from extra
        ArrayList<Event> events = getIntent().getParcelableArrayListExtra(EVENTS_EXTRA);
        ArrayList<Category> categories = getIntent().getParcelableArrayListExtra(CATEGORIES_EXTRA);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, categories));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogHelper.logInfo(position + " clicked");
            }
        });

        // User location
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location latestLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        // Show the view map fragment
        getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                ViewEventsFragment.newInstance(events, categories, new Event.Location(latestLocation.getLatitude(), latestLocation.getLongitude()))).
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null).commit();
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
        if (id == R.id.action_login) {
            startActivity(new Intent(ViewEventsActivity.this, LoginActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDetailsPanelLayout.isPanelExpanded()) {
            mDetailsPanelLayout.collapsePanel();
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
        mEventTitleTextView.setText(event.getCompany().getName() + " " + event.getText().getHeading());
        getFragmentManager().beginTransaction().replace(
                R.id.view_event_fragment, ViewEventDetailsFragment.newInstance(event)).commit();
        mDetailsPanelLayout.expandPanel();
    }

    // Clicking an event in map view
    @Override
    public void onMapEventClick(Event event) {
        mEventTitleTextView.setText(event.getCompany().getName() + " " + event.getText().getHeading());
        getFragmentManager().beginTransaction().replace(
                R.id.view_event_fragment, ViewEventDetailsFragment.newInstance(event)).commit();
        mDetailsPanelLayout.showPanel();
    }

    // Clicking somewhere on the map
    @Override
    public void onMapClick() {
        mDetailsPanelLayout.hidePanel();
    }

}
