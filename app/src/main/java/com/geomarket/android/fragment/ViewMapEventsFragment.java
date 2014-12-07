package com.geomarket.android.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geomarket.android.R;
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

public class ViewMapEventsFragment extends Fragment {
    private static String EVENTS_PARAM = "events_param";
    private static String LOCATION_PARAM = "location_param";

    private OnMapEventClickListener mListener;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private MapFragment mMapFragment;

    private ArrayList<Event> mEvents;
    private Event.Location mLocation;

    private Map<String, Event> mMarkerIdEventMap = new HashMap<String, Event>();

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
        View v = inflater.inflate(R.layout.fragment_view_map_events, view, false);
        mEvents = getArguments().getParcelableArrayList(EVENTS_PARAM);
        mLocation = getArguments().getParcelable(LOCATION_PARAM);
        mMapFragment = MapEventsFragment.newInstance(mEvents, mLocation);
        getFragmentManager().beginTransaction().replace(R.id.map_container, mMapFragment).commit();
        //setUpMap(mEvents, mLocation);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        //setUpMap(mEvents, mLocation);
    }

    private void setUpMap(ArrayList<Event> events, Event.Location location) {
        mMap = mMapFragment.getMap();
        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12.0f));
            for (Event e : events) {
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
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), mMap.getCameraPosition().zoom), 300, null);
                    mListener.onMapEventClick(mMarkerIdEventMap.get(marker.getId()));
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
