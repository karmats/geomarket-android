package com.geomarket.android.api;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a user, used when creating account
 */
public class User implements Parcelable {

    private String username;
    private String password;
    private String gender;
    private Integer age;
    private String googleId;
    private String facebookId;

    public User() {
    }

    public User(Parcel source) {
        Bundle data = source.readBundle(getClass().getClassLoader());
        username = data.getString("username");
        password = data.getString("password");
        gender = data.getString("gender");
        age = data.getInt("age");
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
        data.putString("username", username);
        data.putString("password", password);
        data.putString("gender", gender);
        data.putInt("age", age);
        data.putString("googleId", googleId);
        data.putString("facebookId", facebookId);
        dest.writeBundle(data);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
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
}
