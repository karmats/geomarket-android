package com.geomarket.android.api;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Class to represent an Event
 */
public class Event implements Parcelable {

    private String id;
    private String category;
    private Long expires;
    private String companyName;
    private String eventTyp;
    private Location location;

    public Event() {
    }

    public Event(Parcel source) {
        Bundle data = source.readBundle();
        id = data.getString("id");
        category = data.getString("category");
        expires = data.getLong("expires");
        companyName = data.getString("companyName");
        eventTyp = data.getString("eventTyp");
        Double latitude = data.getDouble("latitude");
        Double longitude = data.getDouble("longitude");
        if (latitude != null && longitude != null) {
            location = new Location(latitude, longitude);
        }
    }

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        public Event createFromParcel(Parcel data) {
            return new Event(data);
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle data = new Bundle();
        data.putString("id", id);
        data.putString("category", category);
        data.putLong("expires", expires);
        data.putString("companyName", companyName);
        data.putString("eventTyp", eventTyp);
        if (location != null) {
            data.putDouble("latitude", location.lat);
            data.putDouble("longitude", location.lon);
        }
        dest.writeBundle(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getExpires() {
        return expires;
    }

    public void setExpires(Long expires) {
        this.expires = expires;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getEventTyp() {
        return eventTyp;
    }

    public void setEventTyp(String eventTyp) {
        this.eventTyp = eventTyp;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return companyName + " category: " + category;
    }

    public static class Location implements Parcelable {
        private Double lat;
        private Double lon;

        public Location() {
        }

        public Location(Parcel source) {
            Bundle data = source.readBundle();
            lat = data.getDouble("lat");
            lon = data.getDouble("lon");
        }

        public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
            public Location createFromParcel(Parcel data) {
                return new Location(data);
            }

            public Location[] newArray(int size) {
                return new Location[size];
            }
        };

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            Bundle data = new Bundle();
            data.putDouble("lat", lat);
            data.putDouble("lon", lon);
            dest.writeBundle(data);
        }

        @Override
        public int describeContents() {
            return 0;
        }


        public Location(Double lat, Double lon) {
            this.lat = lat;
            this.lon = lon;
        }

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLon() {
            return lon;
        }

        public void setLon(Double lon) {
            this.lon = lon;
        }

        public LatLng toLatLng() {
            return new LatLng(lat, lon);
        }

        @Override
        public String toString() {
            return lat + ", " + lon;
        }
    }
}
