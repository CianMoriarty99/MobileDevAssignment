package com.example.assignment112_1.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

public class VisitPoint {
    private String location;
    //private String pressure;
    //private String temperature;

    public VisitPoint(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location= location;
    }
}

