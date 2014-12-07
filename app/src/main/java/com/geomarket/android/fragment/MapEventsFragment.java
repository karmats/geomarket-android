package com.geomarket.android.fragment;


import android.app.Activity;
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
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapEventsFragment extends MapFragment {
    private static final String EVENTS_PARAM = "events_param";
    private static final String LOCATION_PARAM = "location_param";

    private OnMapEventClickListener mListener;

    private ArrayList<Event> mEvents;
    private Event.Location mLocation;

    private Map<String, Event> mMarkerIdEventMap = new HashMap<>();


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param events   The events to show on the map.
     * @param location The initial location.
     * @return A new instance of fragment MapEventsFragment.
     */
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
                    mMarkerIdEventMap.put(m.getId(), e);
                }
            }
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    mListener.onMapClick();
                }
            });
            // When user clicks on a marker, show the event details view
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    // Animate to the marker
                    mListener.onMapEventClick(mMarkerIdEventMap.get(marker.getId()));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), mMap.getCameraPosition().zoom), 300, null);
                    return true;
                }
            });
        } else {
            LogHelper.logError("Google map is null, any chance that Google Services isn't installed?");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMapEventClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnMapEventClickListener {

        /**
         * When user clicks on an {@link com.geomarket.android.api.Event}
         *
         * @param event The event clicked
         */
        public void onMapEventClick(Event event);

        /**
         * When user clicks somewhere on the map (not on marker)
         */
        public void onMapClick();
    }

}
