package com.firstprog.universityitschool.Model;

public class EventsModel {
    String eventMainChildID, eventTitle, eventUrl, eventPriority, eventDate, eventTime, eventDescription;

    public EventsModel(){}

    public EventsModel(String eventMainChildID, String eventTitle, String eventUrl, String eventPriority, String eventDate, String eventTime, String eventDescription) {
        this.eventMainChildID = eventMainChildID;
        this.eventTitle = eventTitle;
        this.eventUrl = eventUrl;
        this.eventPriority = eventPriority;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventDescription = eventDescription;
    }

    public String getEventMainChildID() {
        return eventMainChildID;
    }

    public void setEventMainChildID(String eventMainChildID) {
        this.eventMainChildID = eventMainChildID;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventUrl() {
        return eventUrl;
    }

    public void setEventUrl(String eventUrl) {
        this.eventUrl = eventUrl;
    }

    public String getEventPriority() {
        return eventPriority;
    }

    public void setEventPriority(String eventPriority) {
        this.eventPriority = eventPriority;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }
}
