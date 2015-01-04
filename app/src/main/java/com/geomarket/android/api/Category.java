package com.geomarket.android.api;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class to represent a category
 */
public class Category implements Parcelable {

    private String id;
    private String name;

    public Category() {
    }

    public Category(Parcel source) {
        Bundle data = source.readBundle(getClass().getClassLoader());
        this.id = data.getString("id");
        this.name = data.getString("name");
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
        data.putString("name", name);
        dest.writeBundle(data);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
