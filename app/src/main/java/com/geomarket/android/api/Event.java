package com.geomarket.android.api;

import com.google.android.gms.maps.model.LatLng;

/**
 * Class to represent an Event
 */
public class Event {

    private String id;
    private String category;
    private Long expires;
    private String companyName;
    private String eventTyp;
    private Location location;

    public Event() {
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


    public class Location {
        private Double lat;
        private Double lon;

        public Location() {
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
    }
}
