package com.geomarket.android.api.service;

import com.geomarket.android.api.ApiResult;
import com.geomarket.android.api.AuthenticatedUser;
import com.geomarket.android.api.Event;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Inteface for integration with GeoMarket REST ws
 */
public interface GeoMarketServiceApi {

    static final String EVENTS_SVC_PATH = "/Geomarket/webresources/events/byLocation";
    static final String AUTH_SVC_PATH = "/Geomarket/webresources/auth";

    // Event parameters
    static final String RADIUS_PARAM = "radius";
    static final String LAT_PARAM = "latitude";
    static final String LON_PARAM = "longitude";
    static final String LANG_PARAM = "language";

    // Authenticate parameters
    static final String AUTH_SERVICE_PARAM = "authService";
    static final String AUTH_TOKEN_HEADER = "Auth-Token";

    /**
     * Gets all events near a specific location
     *
     * @param lat    The location lat point
     * @param lon    The location lon point
     * @param radius The radius in meters to fetch events for
     * @param lang   The language
     * @return
     */
    @GET(EVENTS_SVC_PATH)
    ApiResult<List<Event>> getEventsForLocation(@Query(LAT_PARAM) Double lat, @Query(LON_PARAM) Double lon,
                                     @Query(RADIUS_PARAM) Integer radius, @Query(LANG_PARAM) String lang);

    /**
     * Authenticates a user.
     *
     * @param authService The service. Possible values google, facebook
     * @param authToken   The one time token gotten from google or facebook server
     * @return
     */
    @POST(AUTH_SVC_PATH)
    public AuthenticatedUser authenticate(@Query(AUTH_SERVICE_PARAM) String authService, @Header(AUTH_TOKEN_HEADER) String authToken);
}
