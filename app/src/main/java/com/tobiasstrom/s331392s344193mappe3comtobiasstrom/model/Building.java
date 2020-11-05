package com.tobiasstrom.s331392s344193mappe3comtobiasstrom.model;

public class Building {
    private String id;
    private String address;
    private String postalNr;
    private String place;
    private int floors;
    private boolean latLng;

    private String description;
    private int opening;
    private int closing;

    public Building() {
    }



    public Building(String id, String address, String postalNr, String place, int floors, boolean latLng, String description, int opening, int closing) {
        this.id = id;
        this.address = address;
        this.postalNr = postalNr;
        this.place = place;
        this.floors = floors;
        this.latLng = latLng;
        this.description = description;
        this.opening = closing;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalNr() {
        return postalNr;
    }
    public void setPostalNr(String postalNr) {
        this.postalNr = postalNr;
    }

    public String getPlace() {
        return place;
    }
    public void setPlace(String place) {
        this.place = place;
    }

    public int getFloors() {
        return floors;
    }
    public void setFloors(int floors) {
        this.floors = floors;
    }

    public boolean isLatLng() {
        return latLng;
    }
    public void setLatLng(boolean latLng) {
        this.latLng = latLng;
    }


    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public int getOpening() {
        return opening;
    }
    public void setOpening(int opening) {
        this.opening = opening;
    }

    public int getClosing() {
        return closing;
    }
    public void setClosing(int closing) {
        this.closing = closing;
    }
}
