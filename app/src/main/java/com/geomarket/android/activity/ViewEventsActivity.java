package com.geomarket.android.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.geomarket.android.R;
import com.geomarket.android.api.Event;
import com.geomarket.android.fragment.MapEventsFragment;
import com.geomarket.android.fragment.ViewEventDetailsFragment;
import com.geomarket.android.fragment.ViewEventsFragment;
import com.geomarket.android.fragment.ViewListEventsFragment;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ViewEventsActivity extends ActionBarActivity implements ViewListEventsFragment.OnListEventClickListener, MapEventsFragment.OnMapEventClickListener {

    public static final String EVENTS_EXTRA = "events_extra";

    /**
     * The {@link SlidingUpPanelLayout} that will show details about an event.
     */
    SlidingUpPanelLayout mDetailsPanelLayout;

    // Latest known location
    private Location mLatestLocation;
    // The events
    private ArrayList<Event> mEvents = new ArrayList<Event>();

    // To be filled in when event should be shown
    @InjectView(R.id.view_event_detail_title)
    TextView mEventTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);

        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));

        // Get the events from extra
        mEvents = getIntent().getParcelableArrayListExtra(EVENTS_EXTRA);

        // Sliding up panel
        mDetailsPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLatestLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        // Show the view map fragment
        getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                ViewEventsFragment.newInstance(mEvents, new Event.Location(mLatestLocation.getLatitude(), mLatestLocation.getLongitude()))).
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null).commit();

        // Inject the views
        ButterKnife.inject(this);
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

    // Clicking an event in list view
    @Override
    public void onListEventClick(Event event) {
        mEventTitleTextView.setText(event.getCompany().getName() + " " + event.getEventText().getHeading());
        getFragmentManager().beginTransaction().replace(
                R.id.view_event_fragment, ViewEventDetailsFragment.newInstance(event)).commit();
        mDetailsPanelLayout.expandPanel();
    }

    // Clicking an event in map view
    @Override
    public void onMapEventClick(Event event) {
        mEventTitleTextView.setText(event.getCompany().getName() + " " + event.getEventText().getHeading());
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
