package com.geomarket.android.api;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class to represent a category
 */
public class Category implements Parcelable {

    private String id;
    private String defaultName;
    private String description;

    public Category() {
    }

    public Category(Parcel source) {
        Bundle data = source.readBundle(getClass().getClassLoader());
        this.id = data.getString("id");
        this.defaultName = data.getString("defaultName");
        this.description = data.getString("description");
    }

    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        public Category createFromParcel(Parcel data) {
            return new Category(data);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle data = new Bundle();
        data.putString("id", id);
        data.putString("defaultName", defaultName);
        data.putString("description", description);
        dest.writeBundle(data);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
