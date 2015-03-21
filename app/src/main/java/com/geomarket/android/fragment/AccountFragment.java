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

import com.geomarket.android.R;
import com.geomarket.android.activity.IMainActivity;
import com.geomarket.android.util.LogHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Login or create new account
 */
public class AccountFragment extends Fragment {

    // Main activity
    private IMainActivity mMainActivity;

    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    @InjectView(R.id.login_pager)
    ViewPager mViewPager;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections.
     */
    AccountPagerAdapter mAccountPagerAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AccountFragment.
     */
    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public AccountFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account, container, false);
        ButterKnife.inject(this, v);

        // Create the adapter that will return a fragment for each of the two
        // primary sections of the activity.
        mAccountPagerAdapter = new AccountPagerAdapter(getChildFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mAccountPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener());
        mMainActivity.getPagerSlidingTabStrip().setViewPager(mViewPager);
        return v;
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

    @Override
    public void onResume() {
        super.onResume();
        mMainActivity.onAccountView();
    }

    /**
     * A {@link android.support.v13.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class AccountPagerAdapter extends FragmentPagerAdapter {

        public AccountPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return LoginFragment.newInstance();
                case 1:
                    return CreateAccountFragment.newInstance();
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
                    return getString(R.string.title_fragment_login).toUpperCase();
                case 1:
                    return getString(R.string.title_fragment_create_account).toUpperCase();
            }
            return null;
        }

    }
}
