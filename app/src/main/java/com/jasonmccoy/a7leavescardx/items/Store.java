package com.jasonmccoy.a7leavescardx.items;

import com.google.gson.Gson;

import static com.jasonmccoy.a7leavescardx.AppClass.TEST;

public class Store {

    public static final String TAG = TEST + Store.class.getSimpleName();

    private String identifier;
    private double lan;
    private double lat;
    private String note;
    private int radius;

    public Store() {

    }

    public Store(String identifier, double lan, double lat, String note, int radius) {
        this.identifier = identifier;
        this.lan = lan;
        this.lat = lat;
        this.note = note;
        this.radius = radius;
    }

    public String getIdentifier() {
        return identifier;
    }

    public double getLan() {
        return lan;
    }

    public double getLat() {
        return lat;
    }

    public String getNote() {
        return note;
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
