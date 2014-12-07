package com.geomarket.android.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.geomarket.android.R;
import com.geomarket.android.api.Event;
import com.geomarket.android.util.LogHelper;
import com.google.android.gms.maps.MapFragment;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ViewEventsFragment extends Fragment {
    private static String EVENTS_PARAM = "events_param";
    private static String LOCATION_PARAM = "location_param";

    private ArrayList<Event> mEvents;
    private Event.Location mLocation;

    private MapFragment mMapFragment;
    private ViewListEventsFragment mListEventsFragment;

    private ViewType mViewType;

    @InjectView(R.id.map_list_btn)
    ImageButton mToggleMapListBtn;


    public static ViewEventsFragment newInstance(ArrayList<Event> events, Event.Location location) {
        LogHelper.logInfo("Events is " + events + " Location is " + location);
        ViewEventsFragment fragment = new ViewEventsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(EVENTS_PARAM, events);
        args.putParcelable(LOCATION_PARAM, location);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get events and location from arguments
        mEvents = getArguments().getParcelableArrayList(EVENTS_PARAM);
        mLocation = getArguments().getParcelable(LOCATION_PARAM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup view, Bundle bundle) {
        View v = inflater.inflate(R.layout.fragment_view_map_events, view, false);
        ButterKnife.inject(this, v);

        // Set up the map
        mMapFragment = MapEventsFragment.newInstance(mEvents, mLocation);
        getFragmentManager().beginTransaction().replace(R.id.view_events_container, mMapFragment).commit();
        mViewType = ViewType.MAP;

        // Set up the list fragment
        mListEventsFragment = ViewListEventsFragment.newInstance(mEvents);

        return v;
    }

    @OnClick(R.id.map_list_btn)
    public void toggleView() {
        Fragment newFragment = mMapFragment;
        if (mViewType == ViewType.MAP) {
            newFragment = mListEventsFragment;
            mViewType = ViewType.LIST;
            // Set map icon
            mToggleMapListBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_map));
        } else {
            mViewType = ViewType.MAP;
            // Set list icon
            mToggleMapListBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_list));
        }
        getFragmentManager().beginTransaction().replace(R.id.view_events_container, newFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }

    /**
     * Enum to describe current view type
     */
    private enum ViewType {
        MAP, LIST
    }
}
