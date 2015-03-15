package com.geomarket.android.api;

/**
 * Object to use for authentication to dibbler
 */
public class AuthUser {
    private final String username;
    private final String password;
    private final String facebookId;
    private final String googleId;

    public AuthUser(String username, String password, String facebookId, String googleId) {
        this.username = username;
        this.password = password;
        this.facebookId = facebookId;
        this.googleId = googleId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public String getGoogleId() {
        return googleId;
    }
}
