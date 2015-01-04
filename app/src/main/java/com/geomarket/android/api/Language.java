package com.geomarket.android.api;

/**
 * Class to represent a Language.
 */
public class Language {
    public static final String PREF_LANGUAGE_ID = "language_id";

    private String id;
    private String shortName;
    private String fullName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
