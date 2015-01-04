package com.geomarket.android.api.service;

import retrofit.RestAdapter;

/**
 * Builder to use when creating new instance of GeoMarketServiceApi
 */
public class GeoMarketServiceApiBuilder {

    public static final String HOST = "http://109.74.2.250";
    public static final String PORT = "7200";

    private GeoMarketServiceApiBuilder() {
    }

    public static GeoMarketServiceApi newInstance() {
        return new RestAdapter.Builder()
                .setEndpoint(HOST + ":" + PORT)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build()
                .create(GeoMarketServiceApi.class);
    }
}
