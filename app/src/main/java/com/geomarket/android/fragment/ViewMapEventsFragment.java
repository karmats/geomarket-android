package com.geomarket.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geomarket.android.activity.ViewEventsActivity;
import com.geomarket.android.api.Event;
import com.geomarket.android.util.LogHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class ViewMapEventsFragment extends SupportMapFragment {
    private static String EVENTS_PARAM = "events_param";
    private static String LOCATION_PARAM = "location_param";

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    public static ViewMapEventsFragment newInstance(ArrayList<Event> events, Event.Location location) {
        LogHelper.logInfo("Events is " + events + " Location is " + location);
        ViewMapEventsFragment fragment = new ViewMapEventsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(EVENTS_PARAM, events);
        args.putParcelable(LOCATION_PARAM, location);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup view, Bundle bundle) {
        View v = super.onCreateView(inflater, view, bundle);
        ArrayList<Event> events = getArguments().getParcelableArrayList(EVENTS_PARAM);
        Event.Location location = getArguments().getParcelable(LOCATION_PARAM);
        setUpMap(events, location);
        return v;
    }

    private void setUpMap(ArrayList<Event> events, Event.Location location) {
        mMap = getMap();
        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLat(), location.getLon()), 12.0f));
            for (Event e : events) {
                LogHelper.logInfo("Location from event: " + e.getLocation());
                if (e.getLocation() != null) {
                    mMap.addMarker(new MarkerOptions().position(e.getLocation().toLatLng()).title(e.getCompanyName()));
                }
            }
        } else {
            LogHelper.logError("Google map is null, any chance that Google Services isn't installed?");
        }
        // When user clicks on a marker, show the event details view
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                ((ViewEventsActivity)getActivity()).viewEvent(null);
                return true;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                LogHelper.logInfo("Map clicked!");
                ((ViewEventsActivity)getActivity()).viewEvent(null);
            }
        });
    }

}
