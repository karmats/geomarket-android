package com.geomarket.android.api.service;

import com.geomarket.android.api.ApiResult;
import com.geomarket.android.api.AuthenticatedUser;
import com.geomarket.android.api.Category;
import com.geomarket.android.api.Event;
import com.geomarket.android.api.Language;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Inteface for integration with GeoMarket REST ws
 */
public interface GeoMarketServiceApi {

    static final String BASE_SVC_PATH = "/dibbler/resource";
    static final String EVENTS_SVC_PATH = BASE_SVC_PATH + "/events/byLocation";
    static final String CATEGORIES_SVC_PATH = BASE_SVC_PATH + "/categories";
    static final String CATEGORIES_BY_LANGUAGE_SVC_PATH = BASE_SVC_PATH + "/categories/language/{langId}";
    static final String LANGUAGES_SVC_PATH = BASE_SVC_PATH + "/languages";
    static final String AUTH_SVC_PATH = BASE_SVC_PATH + "/auth";

    // Event parameters
    static final String RADIUS_PARAM = "radius";
    static final String LAT_PARAM = "latitude";
    static final String LON_PARAM = "longitude";
    static final String LANG_PARAM = "language";

    // Authenticate parameters
    static final String AUTH_SERVICE_PARAM = "authService";
    static final String AUTH_TOKEN_HEADER = "Auth-Token";

    /**
     * Gets all events near a specific location.
     *
     * @param lat    The location lat point
     * @param lon    The location lon point
     * @param radius The radius in meters to fetch events for
     * @param lang   The language
     * @return ApiResults
     */
    @GET(EVENTS_SVC_PATH)
    ApiResult<List<Event>> getEventsForLocation(@Query(LAT_PARAM) Double lat, @Query(LON_PARAM) Double lon,
                                                @Query(RADIUS_PARAM) Integer radius, @Query(LANG_PARAM) String lang);

    /**
     * Get all GeoMarket categories.
     *
     * @return ApiResult
     */
    @GET(CATEGORIES_SVC_PATH)
    ApiResult<List<Category>> getCategories();

    /**
     * Get all GeoMarket categories for a specific language.
     *
     * @param languageId Id of the language fetched from getLanguages()
     * @return ApiResult
     */
    @GET(CATEGORIES_BY_LANGUAGE_SVC_PATH)
    ApiResult<List<Category>> getCategoriesByLanguage(@Path("langId") String languageId);

    /**
     * Get all GeoMarket supported languages.
     *
     * @return ApiResult
     */
    @GET(LANGUAGES_SVC_PATH)
    ApiResult<List<Language>> getLanguages();

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
