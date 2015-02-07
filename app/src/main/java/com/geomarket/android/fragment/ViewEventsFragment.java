package com.geomarket.android.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.geomarket.android.R;
import com.geomarket.android.api.Category;
import com.geomarket.android.api.Event;
import com.geomarket.android.util.LogHelper;
import com.google.android.gms.maps.MapFragment;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ViewEventsFragment extends Fragment {
    private static final String EVENTS_PARAM = "events_param";
    private static final String CATEGORIES_PARAM = "categories_param";
    private static final String LOCATION_PARAM = "location_param";

    private ArrayList<Event> mEvents;
    private ArrayList<Category> mCategories;
    private Event.Location mLocation;

    private MapFragment mMapFragment;
    private ViewListEventsFragment mListEventsFragment;

    private ViewType mViewType;

    private OnLayoutChangeListener mListener;

    @InjectView(R.id.map_list_btn)
    ImageButton mToggleMapListBtn;


    public static ViewEventsFragment newInstance(ArrayList<Event> events, ArrayList<Category> categories, Event.Location location) {
        LogHelper.logInfo("Events is " + events + " Location is " + location);
        ViewEventsFragment fragment = new ViewEventsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(EVENTS_PARAM, events);
        args.putParcelable(LOCATION_PARAM, location);
        args.putParcelableArrayList(CATEGORIES_PARAM, categories);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get events and location from arguments
        mEvents = getArguments().getParcelableArrayList(EVENTS_PARAM);
        mLocation = getArguments().getParcelable(LOCATION_PARAM);
        mCategories = getArguments().getParcelableArrayList(CATEGORIES_PARAM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup view, Bundle bundle) {
        View v = inflater.inflate(R.layout.fragment_view_events, view, false);
        ButterKnife.inject(this, v);

        // Set up the map
        mMapFragment = MapEventsFragment.newInstance(mEvents, mCategories, mLocation);

        // Set up the list fragment
        mListEventsFragment = ViewListEventsFragment.newInstance(mEvents);

        getFragmentManager().beginTransaction().replace(R.id.view_events_container, mMapFragment).commit();
        mViewType = ViewType.MAP;
        return v;
    }

    @OnClick(R.id.map_list_btn)
    public void toggleView() {
        Fragment newFragment = mMapFragment;
        if (mViewType == ViewType.MAP) {
            newFragment = mListEventsFragment;
            mViewType = ViewType.LIST;
            // Set map icon
            mToggleMapListBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_icon_map));
            mListener.onLayoutChange(ViewType.LIST);
        } else {
            mViewType = ViewType.MAP;
            // Set list icon
            mToggleMapListBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_icon_list));
            mListener.onLayoutChange(ViewType.MAP);
        }
        getFragmentManager().beginTransaction().replace(R.id.view_events_container, newFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnLayoutChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLayoutChangedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Enum to describe current view type
     */
    public enum ViewType {
        MAP, LIST
    }

    /**
     * When layout changes
     */
    public interface OnLayoutChangeListener {
        /**
         * Called when the layout has changed
         *
         * @param type The ViewType layout it has changed to
         */
        void onLayoutChange(ViewType type);
    }
}
