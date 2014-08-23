package com.geomarket.android.util;

import android.util.Log;

/**
 * Utility class used for logging to the android Log
 */
public class LogHelper {

    public static final String LOG_TAG = "GeoMarket";

    // Private constructor so we can't initiate this class
    private LogHelper() {}

    /**
     * Log debug message
     */
    public static void logDebug(String message) {
        Log.d(LOG_TAG, message);
    }

    /**
     * Log info message
     */
    public static void logInfo(String message) {
        Log.i(LOG_TAG, message);
    }

    /**
     * Log error message
     */
    public static void logError(String message) {
        Log.e(LOG_TAG, message);
    }

    /**
     * Log an exception
     *
     * @param e
     *            The exception to log
     */
    public static void logException(Exception e) {
        Log.wtf(LOG_TAG, e);
    }
}
