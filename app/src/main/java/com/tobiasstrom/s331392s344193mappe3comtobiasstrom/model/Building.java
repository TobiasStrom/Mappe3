package com.tobiasstrom.s331392s344193mappe3comtobiasstrom.model;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Building {
    //variablen vi bruker
    private String id;
    private String title;
    private String address;
    private String addressNr;
    private String postalNr;
    private String place;
    private int floors;
    private double lat;
    private double lng;
    private String description;
    private Date opening;
    private Date closing;
    private LatLng latLng;

    public Building() {
    }

    public Building(String id, String address, String postalNr, String place, int floors, double lat, double lng, String description, Date opening, Date closing) {
        this.id = id;
        this.address = address;
        this.postalNr = postalNr;
        this.place = place;
        this.floors = floors;
        this.lat = lat;
        this.lng = lng;
        this.description = description;
        this.opening = opening;
        this.closing = closing;
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

    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }
    public void setLng(double lng) {
        this.lng = lng;
    }

    //Koverterer lat og long til en felles latlng
    public LatLng getLatLng() {
        return latLng = new LatLng(getLat(),getLng());
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Date getOpening() {
        return opening;
    }

    //Konverterer string til rikig Dateform
    public void setOpening(String opening) {
        Date openings = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            openings = sdf.parse(opening);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.opening = openings;
    }

    public Date getClosing() {
        return closing;
    }

    //Konverterer string til rikig Dateform
    public void setClosing(String closing) {
        Date opening = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            opening = sdf.parse(closing);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.closing = opening;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddressNr() {
        return addressNr;
    }

    public void setAddressNr(String addressNr) {
        this.addressNr = addressNr;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    public String toString() {
        return "Building{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", address='" + address + '\'' +
                ", addressNr='" + addressNr + '\'' +
                ", postalNr='" + postalNr + '\'' +
                ", place='" + place + '\'' +
                ", floors=" + floors +
                ", lat=" + lat +
                ", lng=" + lng +
                ", description='" + description + '\'' +
                ", opening=" + opening +
                ", closing=" + closing +
                ", latLng=" + latLng +
                '}';
    }
}
