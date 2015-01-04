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
    private String categoryId;
    private Long expires;
    private String eventTypeId;
    private Location location;
    private Text text;
    private Company company;

    public Event() {
    }

    public Event(Parcel source) {
        Bundle data = source.readBundle(getClass().getClassLoader());
        id = data.getString("id");
        categoryId = data.getString("categoryId");
        expires = data.getLong("expires");
        eventTypeId = data.getString("eventTypeId");
        location = data.getParcelable("location");
        text = data.getParcelable("text");
        company = data.getParcelable("company");
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
        data.putString("categoryId", categoryId);
        data.putLong("expires", expires);
        data.putString("eventTypeId", eventTypeId);
        data.putParcelable("location", location);
        data.putParcelable("text", text);
        data.putParcelable("company", company);
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Long getExpires() {
        return expires;
    }

    public void setExpires(Long expires) {
        this.expires = expires;
    }

    public String getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(String eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @Override
    public String toString() {
        return company.getName() + " Event: " + text.getHeading();
    }

    /**
     * Describes a location with a latitude and longitude
     */
    public static class Location implements Parcelable {
        private Double latitude;
        private Double longitude;

        public Location() {
        }

        public Location(Parcel source) {
            Bundle data = source.readBundle();
            latitude = data.getDouble("latitude");
            longitude = data.getDouble("longitude");
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
            data.putDouble("latitude", latitude);
            data.putDouble("longitude", longitude);
            dest.writeBundle(data);
        }

        @Override
        public int describeContents() {
            return 0;
        }


        public Location(Double lat, Double lon) {
            this.latitude = lat;
            this.longitude = lon;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double lat) {
            this.latitude = lat;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLon(Double lon) {
            this.longitude = lon;
        }

        public LatLng toLatLng() {
            return new LatLng(latitude, longitude);
        }

        @Override
        public String toString() {
            return latitude + ", " + longitude;
        }
    }

    /**
     * Describes a json event text with a heading and body
     */
    public static class Text implements Parcelable {
        private String heading;
        private String body;

        public Text() {
        }

        public Text(Parcel source) {
            Bundle data = source.readBundle();
            heading = data.getString("heading");
            body = data.getString("body");
        }

        public static final Parcelable.Creator<Text> CREATOR = new Parcelable.Creator<Text>() {
            public Text createFromParcel(Parcel data) {
                return new Text(data);
            }

            public Text[] newArray(int size) {
                return new Text[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            Bundle data = new Bundle();
            data.putString("heading", heading);
            data.putString("body", body);
            dest.writeBundle(data);
        }

        public String getHeading() {
            return heading;
        }

        public void setHeading(String heading) {
            this.heading = heading;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }

    /**
     * Describes a json Company
     */
    public static class Company implements Parcelable {
        private String id;
        private String name;
        private String street;
        private String streetNr;
        private String city;
        private String state;
        private String country;
        private Long postalCode;
        private String www;

        public Company() {
        }

        public Company(Parcel source) {
            Bundle data = source.readBundle();
            id = data.getString("id");
            name = data.getString("name");
            street = data.getString("street");
            streetNr = data.getString("streetNr");
            city = data.getString("city");
            state = data.getString("state");
            country = data.getString("country");
            postalCode = data.getLong("postalCode");
            www = data.getString("www");
        }

        public static final Parcelable.Creator<Company> CREATOR = new Parcelable.Creator<Company>() {
            public Company createFromParcel(Parcel data) {
                return new Company(data);
            }

            public Company[] newArray(int size) {
                return new Company[size];
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
            data.putString("street", street);
            data.putString("streetNr", streetNr);
            data.putString("city", city);
            data.putString("state", state);
            data.putString("country", country);
            data.putLong("postalCode", postalCode == null ? -1 : postalCode);
            data.putString("www", www);
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

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getStreetNr() {
            return streetNr;
        }

        public void setStreetNr(String streetNr) {
            this.streetNr = streetNr;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public Long getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(Long postalCode) {
            this.postalCode = postalCode;
        }

        public String getWww() {
            return www;
        }

        public void setWww(String www) {
            this.www = www;
        }

    }
}
