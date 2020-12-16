/*
 * This code has been adapted from the code developed by Fabio Ciravegna for COM4510 Software Development for Mobile Devices
 * Original copyright notice:
 * Copyright (c) 2020. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package com.example.assignment112_1;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides methods to create and use any type of sensor.
 * It provides methods to create and initialise the sensor as well as methods to start and stop the
 * sensors.
 * Finally, it provides a method to send the data points collected by the sensor.
 */

public class CommonSensor {
    private String TAG;
    private List<Float> sensorDataList;

    private SensorEventListener mSensorListener = null;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private long mSamplingRateInMSecs;
    private long mSamplingRateNano;
    private long SENSOR_READING_FREQUENCY = 10000;
    private long timePhoneWasLastRebooted = 0;
    private long lastReportTime = 0;
    private boolean started;

    /**
     * creates a CommonSensor object.
     * @param context the Activity in which you want this sensor to operate
     * @param sensorType an int representing the type of the sensor needed, as listed in
     *                   https://developer.android.com/guide/topics/sensors/sensors_overview
     * @param TAG a tag given to the sensor for debugging purposes
     */
    public CommonSensor(Context context, int sensorType, String TAG) {
        timePhoneWasLastRebooted = System.currentTimeMillis() - SystemClock.elapsedRealtime();
        mSamplingRateNano = (long) (SENSOR_READING_FREQUENCY) * 1000000;
        mSamplingRateInMSecs = (long) SENSOR_READING_FREQUENCY;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(sensorType);
        this.TAG = TAG;
        this.sensorDataList = new ArrayList<>();
        initSensorListener();
    }

    /**
     * Checks whether or not the sensor is available so it can be started.
     * If it can be started then this method initiates the listener to receive the data points sent
     * out by the sensors.
     */
    private void initSensorListener() {
        if (!standardSensorAvailable()) {
            Log.d(TAG, "Standard " + TAG + " unavailable");
        } else {
            Log.d(TAG, "Using " + TAG + "");
            mSensorListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    // this is needed so the that the location and sensor readings are only updated
                    // every 20 seconds
                    long diff = event.timestamp - lastReportTime;
                    if (diff >= mSamplingRateNano) {
                        long actualTimeInMseconds = timePhoneWasLastRebooted + (long) (event.timestamp / 1000000.0);
                        // can be acceleration, temperature, or pressure for this use case
                        float sensorValue = event.values[0];
                        int accuracy = event.accuracy;
                        lastReportTime = event.timestamp;
                        if (TAG.equals("Thermometer")) TrackingActivity.setTemperature(sensorValue);
                        if (TAG.equals("Barometer")) TrackingActivity.setPressure(sensorValue);
                    }
                }
                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    Log.i(TAG, "Accuracy changed to " + accuracy + " for " + TAG + "");
                }
            };
        }
    }

    /**
     * Returns whether or not the sensor is available to use.
     */
    public boolean standardSensorAvailable() {
        return (mSensor != null);
    }

    /**
     * Starts the sensor by registering it with the sensor manager and giving it a data collection
     * rate.
     */
    public void startSensing() {
        if (standardSensorAvailable()) {
            Log.d("Standard " + TAG + "", "starting listener");
            mSensorManager.registerListener(mSensorListener, mSensor, (int) (mSamplingRateInMSecs * 1000));
            setStarted(true);
        } else {
            Log.i(TAG, "sensor unavailable or already active");
        }
    }

    /**
     * Stops the sensor by unregistering it from the sensor manager and stops collecting data.
     */
    public void stopSensor() {
        if (standardSensorAvailable()) {
            Log.d("Standard " + TAG + "", "stopping listener");
            try {
                mSensorManager.unregisterListener(mSensorListener);
            } catch (Exception e) {
                Log.d(TAG, "check if sensor is already unregistered");
            }
        }
        setStarted(false);
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public List<Float> getSensorDataList() {
        return sensorDataList;
    }

}
