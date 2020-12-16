package com.example.assignment112_1.model;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

/**
 * This class provides a structure for the visit points that can be saved in the database. Each data
 * point contains the location, temperature, and pressure readings for the points on the visit path.
 */

public class VisitPoint {
    private float[] location;
    private Float pressure;
    private Float temperature;

    public VisitPoint(float[] location, Float temperature, Float pressure) {
        this.location = location;
        this.pressure = pressure;
        this.temperature = temperature;
    }

    public float[] getLocation() {
        return location;
    }
    public void setLocation(float[] location) {
        this.location= location;
    }

    public Float getTemperature() {
        return temperature;
    }
    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public Float getPressure() {
        return pressure;
    }
    public void setPressure(Float pressure) {
        this.pressure = pressure;
    }
}

