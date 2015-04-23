package com.geomarket.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.geomarket.android.R;
import com.geomarket.android.activity.IMainActivity;
import com.geomarket.android.api.Category;
import com.geomarket.android.api.Event;
import com.geomarket.android.util.LogHelper;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ViewEventsFragment extends Fragment {
    public static final String TAG_NAME = "view_events_fragment_tag";

    private static final String EVENTS_PARAM = "events_param";
    private static final String CATEGORIES_PARAM = "categories_param";
    private static final String LOCATION_PARAM = "location_param";

    private ArrayList<Event> mEvents;
    private ArrayList<Category> mCategories;
    private Event.Location mLocation;

    // Main activity
    private IMainActivity mMainActivity;

    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    @InjectView(R.id.pager)
    ViewPager mViewPager;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections.
     */
    EventsPagerAdapter mEventsPagerAdapter;

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
        mEventsPagerAdapter = new EventsPagerAdapter(getChildFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mEventsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener());
        mMainActivity.getPagerSlidingTabStrip().setViewPager(mViewPager);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMainActivity.onViewEventsView();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mMainActivity = (IMainActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLayoutChangedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMainActivity = null;
    }

    /**
     * Selects the list view and filter events
     *
     * @param filter Filter string
     */
    public void goToListViewAndFilterEvents(String filter) {
        mViewPager.setCurrentItem(1);
        mEventsPagerAdapter.filterEvents(filter);
    }

    /**
     * A {@link android.support.v13.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class EventsPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider {
        private MapEventsFragment mapEventsFragment;
        private ViewListEventsFragment viewListEventsFragment;

        public EventsPagerAdapter(FragmentManager fm) {
            super(fm);
            mapEventsFragment = MapEventsFragment.newInstance(mEvents, mCategories,
                    new Event.Location(mLocation.getLatitude(), mLocation.getLongitude()));
            viewListEventsFragment = ViewListEventsFragment.newInstance(mEvents);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return mapEventsFragment;

                case 1:
                    return viewListEventsFragment;
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

        @Override
        public int getPageIconResId(int position) {
            switch (position) {
                case 0:
                    return R.drawable.ic_action_icon_map;
                case 1:
                    return R.drawable.ic_action_icon_list;
            }
            return 0;
        }

        /**
         * Filter events
         *
         * @param filter Filter string
         */
        public void filterEvents(String filter) {
            viewListEventsFragment.filterEvents(filter);
        }
    }

}
