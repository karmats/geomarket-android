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

    void showEventControls();

    void hideEventControls();
}
