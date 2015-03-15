package com.geomarket.android.api.service;

import com.geomarket.android.api.ApiResult;
import com.geomarket.android.api.AuthUser;
import com.geomarket.android.api.Category;
import com.geomarket.android.api.Event;
import com.geomarket.android.api.Language;
import com.geomarket.android.api.User;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.GET;
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
    static final String CREATE_USER_PATH = BASE_SVC_PATH + "/users";
    static final String AUTHENTICATE_USER_PATH = BASE_SVC_PATH + "/users/authenticate";

    // Event parameters
    static final String RADIUS_PARAM = "radius";
    static final String LAT_PARAM = "latitude";
    static final String LON_PARAM = "longitude";
    static final String LANG_PARAM = "language";

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
     * Creates a new user. This is a one time operation in Dibbler
     *
     * @return The id of the created User
     */
    @POST(CREATE_USER_PATH)
    ApiResult<String> createNewUser(@Body User user);

    /**
     * Authenticates a user to
     *
     * @param authUser The user to authenticate
     * @return A User object
     */
    @POST(AUTHENTICATE_USER_PATH)
    ApiResult<User> authenticateUser(@Body AuthUser authUser);
}
