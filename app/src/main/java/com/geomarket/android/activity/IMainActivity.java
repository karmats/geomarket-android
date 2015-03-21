package com.geomarket.android.activity;

import com.astuetz.PagerSlidingTabStrip;

/**
 * Exposed functions for main activity for fragment interaction
 */
public interface IMainActivity {

    /**
     * Get the PagerSlidingTabStrip
     */
    PagerSlidingTabStrip getPagerSlidingTabStrip();

    /**
     * Show the controls under toolbar
     */
    void showEventControls();

    /**
     * Hide the controls under toolbar
     */
    void hideEventControls();
}
