package com.geomarket.android.fragment;


import android.app.Fragment;
import android.os.Bundle;

import com.geomarket.android.api.Event;
import com.geomarket.android.util.LogHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapEventsFragment extends MapFragment {
    private static final String EVENTS_PARAM = "events_param";
    private static final String LOCATION_PARAM = "location_param";

    private ArrayList<Event> mEvents;
    private Event.Location mLocation;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param events   Parameter 1.
     * @param location Parameter 2.
     * @return A new instance of fragment MapEventsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapEventsFragment newInstance(ArrayList<Event> events, Event.Location location) {
        MapEventsFragment fragment = new MapEventsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(EVENTS_PARAM, events);
        args.putParcelable(LOCATION_PARAM, location);
        fragment.setArguments(args);
        return fragment;
    }

    public MapEventsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEvents = getArguments().getParcelableArrayList(EVENTS_PARAM);
            mLocation = getArguments().getParcelable(LOCATION_PARAM);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final GoogleMap mMap = getMap();
        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 12.0f));
            for (Event e : mEvents) {
                LogHelper.logInfo("Location from event: " + e.getLocation());
                if (e.getLocation() != null) {
                    Marker m = mMap.addMarker(new MarkerOptions().position(e.getLocation().toLatLng()));
                }
            }
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {

                }
            });
            // When user clicks on a marker, show the event details view
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    // Animate to the marker
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), mMap.getCameraPosition().zoom), 300, null);
                    return true;
                }
            });
        } else {
            LogHelper.logError("Google map is null, any chance that Google Services isn't installed?");
        }
    }
}
