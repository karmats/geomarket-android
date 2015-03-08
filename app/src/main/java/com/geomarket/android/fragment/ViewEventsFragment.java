package com.geomarket.android.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.geomarket.android.R;
import com.geomarket.android.api.Category;
import com.geomarket.android.api.Event;
import com.geomarket.android.util.LogHelper;
import com.geomarket.android.view.SlidingTabLayout;

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

    // The callback listener
    private OnViewEventsListener mListener;

    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    @InjectView(R.id.pager)
    ViewPager mViewPager;

    /**
     * The {@link com.geomarket.android.view.SlidingTabLayout} for tab indication.
     */
    @InjectView(R.id.sliding_tabs)
    SlidingTabLayout mSlidingTabLayout;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections.
     */
    ImagePagerAdapter mSectionsPagerAdapter;

    @InjectView(R.id.details_btn_view)
    LinearLayout mButtonView;
    @InjectView(R.id.details_next_btn)
    Button mNextButton;
    @InjectView(R.id.details_prev_btn)
    Button mPreviousButton;


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

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new ImagePagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.light_orange));
        mSlidingTabLayout.setCustomTabView(R.layout.tab_item, 0, R.id.tab_item_img);
        mSlidingTabLayout.setViewPager(mViewPager);

        return v;
    }

    @OnClick(R.id.details_next_btn)
    public void onNextButtonClicked() {
        mListener.viewNextEvent();

    }

    @OnClick(R.id.details_prev_btn)
    public void onPreviousButtonClicked() {
        mListener.viewPreviousEvent();
    }

    // Called from main activity
    public void onViewEventDetail() {
        mButtonView.setVisibility(View.VISIBLE);
        mSlidingTabLayout.setVisibility(View.GONE);
    }

    public void onHideEventDetail() {
        mButtonView.setVisibility(View.GONE);
        mSlidingTabLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnViewEventsListener) activity;
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
     * A {@link android.support.v13.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class ImagePagerAdapter extends FragmentPagerAdapter {

        public ImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return MapEventsFragment.newInstance(mEvents, mCategories,
                            new Event.Location(mLocation.getLatitude(), mLocation.getLongitude()));

                case 1:
                    return ViewListEventsFragment.newInstance(mEvents);
            }
            LogHelper.logError("Something is terribly wrong, got tab position " + position);
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.title_fragment_view_map_events).toUpperCase();
                case 1:
                    return getString(R.string.title_fragment_view_list_events).toUpperCase();
            }
            return null;
        }

        public Drawable getPageDrawable(int position) {
            switch (position) {
                case 0:
                    return getResources().getDrawable(R.drawable.ic_action_icon_map);
                case 1:
                    return getResources().getDrawable(R.drawable.ic_action_icon_list);
            }
            return null;
        }
    }

    /**
     * Interface for communication with this fragment
     */
    public interface OnViewEventsListener {

        /**
         * View the next event
         */
        public void viewNextEvent();

        /**
         * View previous event
         */
        public void viewPreviousEvent();
    }
}
