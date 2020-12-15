/*
 * Copyright (c) 2018. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package com.example.assignment112_1;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;

import com.example.assignment112_1.model.VisitPoint;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class LocationIntentService extends IntentService {
    private List<Location> locations;

    public LocationIntentService(String name) {
        super(name);
    }

    public LocationIntentService() {
        super("Location Intent");
    }

    /**
     * called when a location is recognised
     *
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (LocationResult.hasResult(intent)) {
            LocationResult locResults = LocationResult.extractResult(intent);
            if (locResults != null) {
                for (Location location : locResults.getLocations()) {
                    if (location == null) continue;
                    if (locations == null) locations = new ArrayList<>();
                    locations.add(location);

                    // check if the activity has not been closed in the meantime
                    if (TrackingActivity.getActivity()!=null)
                        // any modification of the user interface must be done on the UI Thread. The Intent Service is running
                        // in its own thread, so it cannot communicate with the UI.
                        TrackingActivity.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    TrackingActivity.setLocation(location);

                                    LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                                    Float[] locationArray = new Float[]{(float) location.getLatitude(), (float) location.getLongitude()};

                                    VisitPoint visitPoint;
                                    Float temp = TrackingActivity.getTemperature();
                                    Float pressure = TrackingActivity.getPressure();
                                    Log.e("LocationService", "Sensor readings "+temp+", "+pressure);
                                    visitPoint = new VisitPoint(locationArray, temp, pressure);

                                    TrackingActivity.addToPointsList(visitPoint);
                                    if (TrackingActivity.getMap() != null)
                                        Log.e("LocationService", "Marked on map");
                                        TrackingActivity.getMap().addMarker(new MarkerOptions().position(loc)
                                                .title(DateFormat.getTimeInstance().format(new Date())));
                                        // it centres the camera around the new location and zooms in
                                        TrackingActivity.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 18.0f));

                                        //Draws the route
                                        List<VisitPoint> pointsList = TrackingActivity.getPointsList();
                                        ArrayList<LatLng> points = new ArrayList<>();
                                        for (VisitPoint point : pointsList) {
                                            points.add(new LatLng(point.getLocation()[0], point.getLocation()[1]));
                                        }

                                        //Add path between points
                                        PolylineOptions lineOptions= new PolylineOptions()
                                            .addAll(points)
                                            .width(5)
                                            .color(Color.RED);
                                        TrackingActivity.getMap().addPolyline(lineOptions);

                                } catch (Exception e ){
                                    Log.e("LocationService", "Error cannot write on map "+e.getMessage());
                                }
                            }
                        });
                }
            }

        }
    }
}

