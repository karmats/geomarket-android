package com.geomarket.android.api;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.facebook.model.GraphUser;
import com.google.android.gms.plus.model.people.Person;

/**
 * Represents a user, used when creating account
 */
public class User implements Parcelable {
    public static final String GENDER_MALE = "M";
    public static final String GENDER_FEMALE = "F";

    private String displayName;
    private String email;
    private String password;
    private String gender;
    private Long birthday;
    private String googleId;
    private String facebookId;

    public User() {
    }

    public User(Parcel source) {
        Bundle data = source.readBundle(getClass().getClassLoader());
        displayName = data.getString("displayName");
        email = data.getString("email");
        password = data.getString("password");
        gender = data.getString("gender");
        birthday = data.getLong("birthday");
        googleId = data.getString("googleId");
        facebookId = data.getString("facebookId");
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel data) {
            return new User(data);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle data = new Bundle();
        data.putString("displayName", displayName);
        data.putString("email", email);
        data.putString("password", password);
        data.putString("gender", gender);
        data.putLong("birthday", birthday);
        data.putString("googleId", googleId);
        data.putString("facebookId", facebookId);
        dest.writeBundle(data);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    /**
     * Converts a GraphUser to a User
     *
     * @param fbUser The GraphUser to convert
     * @return A User
     */
    public static User fromFacebookUser(GraphUser fbUser) {
        User result = new User();
        result.setFacebookId(fbUser.getId());
        result.setDisplayName(fbUser.getFirstName() + " " + fbUser.getLastName());
        result.setEmail((String) fbUser.getProperty("email"));
        // TODO birthday/age interval
        if ("male".equalsIgnoreCase((String) fbUser.getProperty("gender"))) {
            result.setGender(GENDER_MALE);
        } else {
            result.setGender(GENDER_FEMALE);
        }

        return result;
    }

    /**
     * Creates a User from a google Person object.
     *
     * @param googleUser The google Person
     * @param email      The email of the user, since that can't be retrieved by the google Person
     * @return User
     */
    public static User fromGoogleUser(Person googleUser, String email) {
        User result = new User();
        result.setGoogleId(googleUser.getId());
        result.setDisplayName(googleUser.getDisplayName());
        result.setEmail(email);
        // TODO birthday/age interval
        if (googleUser.getGender() == Person.Gender.MALE) {
            result.setGender(GENDER_MALE);
        } else {
            result.setGender(GENDER_FEMALE);
        }
        return result;
    }
}
