package com.geomarket.android.api;

/**
 * Class to represent an Event
 */
public class Event {

    private String id;
    private String category;
    private Long expires;
    private String companyName;
    private String eventTyp;

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
}
