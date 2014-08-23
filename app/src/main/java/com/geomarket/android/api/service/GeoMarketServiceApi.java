package com.geomarket.android.api.service;

import com.geomarket.android.api.Event;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Inteface for integration with GeoMarket REST ws
 */
public interface GeoMarketServiceApi {

    static final String EVENTS_SVC_PATH = "/v1/events";

    static final String RADIUS_PARAM = "radius";
    static final String LAT_PARAM = "lat";
    static final String LON_PARAM = "lon";
    static final String LANG_PARAM = "lang";

    /**
     * Gets all events near a specific location
     *
     * @param lat  The location lat point
     * @param lon  The location lon point
     * @param radius The radius in meters to fetch events for
     * @param lang The language
     * @return
     */
    @GET(EVENTS_SVC_PATH)
    List<Event> getEventsForLocation(@Query(LAT_PARAM) Double lat, @Query(LON_PARAM) Double lon,
                                     @Query(RADIUS_PARAM) Integer radius, @Query(LANG_PARAM) String lang);
}
