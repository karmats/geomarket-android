package com.geomarket.android.fragment;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.geomarket.android.api.Category;
import com.geomarket.android.api.Event;
import com.geomarket.android.api.service.GeoMarketServiceApiBuilder;
import com.geomarket.android.util.LogHelper;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapEventsFragment extends SupportMapFragment implements GoogleMap.OnCameraChangeListener {
    private static final String EVENTS_PARAM = "events_param";
    private static final String CATEGORIES_PARAM = "categories_param";
    private static final String LOCATION_PARAM = "location_param";

    // The breakpoint for when the marker icons are changed to company icons
    private static final float ZOOM_LEVEL_COMPANY_ICONS = 15.0f;

    private OnMapEventClickListener mListener;

    private ArrayList<Event> mEvents;
    private Event.Location mLocation;
    private ArrayList<Category> mCategories;
    private GoogleMap mMap;
    private boolean mFirstTime;
    private boolean mCompanyIcons;

    private Map<String, Event> mMarkerIdEventMap = new HashMap<>();
    private Map<String, Marker> mMarkersMap = new HashMap<>();
    private Map<String, Float> mMarkerCategoryColors = new HashMap<>();


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param events     The events to show on the map.
     * @param categories The app categories
     * @param location   The initial location.
     * @return A new instance of fragment MapEventsFragment.
     */
    public static MapEventsFragment newInstance(ArrayList<Event> events, ArrayList<Category> categories, Event.Location location) {
        MapEventsFragment fragment = new MapEventsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(EVENTS_PARAM, events);
        args.putParcelable(LOCATION_PARAM, location);
        args.putParcelableArrayList(CATEGORIES_PARAM, categories);
        fragment.setArguments(args);
        return fragment;
    }

    public MapEventsFragment() {
        super();
        mFirstTime = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEvents = getArguments().getParcelableArrayList(EVENTS_PARAM);
            mLocation = getArguments().getParcelable(LOCATION_PARAM);
            mCategories = getArguments().getParcelableArrayList(CATEGORIES_PARAM);
        }
        // Setup marker color map
        // TODO Find better way :)
        Float[] colors = new Float[]{BitmapDescriptorFactory.HUE_BLUE, BitmapDescriptorFactory.HUE_ORANGE,
                BitmapDescriptorFactory.HUE_RED, BitmapDescriptorFactory.HUE_YELLOW, BitmapDescriptorFactory.HUE_ROSE};
        Iterator<Float> colorIterator = Arrays.asList(colors).iterator();
        Float color = colorIterator.next();
        for (Category category : mCategories) {
            if (!mMarkerCategoryColors.containsKey(category.getId())) {
                mMarkerCategoryColors.put(category.getId(), color);
                if (colorIterator.hasNext()) {
                    color = colorIterator.next();
                }
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mMap == null) {
            mMap = getMap();
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                mMap.setOnCameraChangeListener(this);
                mMap.getUiSettings().setZoomControlsEnabled(false);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(CameraPosition.builder().target(new LatLng(mLocation.getLatitude(),
                        mLocation.getLongitude())).zoom(12.0f).build());
                // Animate the camera if this is the first time, else just move it
                if (mFirstTime) {
                    mMap.animateCamera(cameraUpdate);
                } else {
                    mMap.moveCamera(cameraUpdate);
                }
                for (final Event e : mEvents) {
                    if (e.getLocation() != null) {
                        Marker m = getMap().addMarker(new MarkerOptions().position(e.getLocation().toLatLng()));
                        setMarkerIcon(m, e);
                        mMarkerIdEventMap.put(m.getId(), e);
                        mMarkersMap.put(m.getId(), m);
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
                mFirstTime = false;
            } else {
                LogHelper.logError("Google map is null, any chance that Google Services isn't installed?");
            }
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

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        boolean updateIcons = false;
        if (cameraPosition.zoom >= ZOOM_LEVEL_COMPANY_ICONS && !mCompanyIcons) {
            LogHelper.logInfo("Zoom level reached breakpoint, replacing with company icons");
            mCompanyIcons = true;
            updateIcons = true;
        } else if (cameraPosition.zoom < ZOOM_LEVEL_COMPANY_ICONS && mCompanyIcons) {
            LogHelper.logInfo("Zoom level reached break point, replacing with default icons");
            mCompanyIcons = false;
            updateIcons = true;
        }
        if (updateIcons) {
            for (String markerId : mMarkerIdEventMap.keySet()) {
                setMarkerIcon(mMarkersMap.get(markerId), mMarkerIdEventMap.get(markerId));
            }
        }
    }

    public void setMarkerIcon(final Marker m, final Event e) {
        if (e.getImageSmallUrl() != null && mCompanyIcons) {
            Picasso.with(getActivity()).load(GeoMarketServiceApiBuilder.HOST
                    + e.getImageSmallUrl()).resize(100, 100).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
                    m.setIcon(bitmapDescriptor);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            });
        } else {
            float hue = mMarkerCategoryColors.containsKey(e.getCategoryId()) ? mMarkerCategoryColors.get(e.getCategoryId())
                    : BitmapDescriptorFactory.HUE_RED;
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(hue);
            m.setIcon(bitmapDescriptor);
        }
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
