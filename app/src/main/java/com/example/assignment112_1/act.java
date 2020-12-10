/*
 * Copyright (c) 2019. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package com.example.assignment112_1;

import android.hardware.Sensor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class act extends AppCompatActivity {
    private CommonSensor barometer;
    private CommonSensor thermometer;
    private Accelerometer accelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        barometer = new CommonSensor(this, Sensor.TYPE_PRESSURE, "Barometer");
        thermometer = new CommonSensor(this, Sensor.TYPE_AMBIENT_TEMPERATURE, "Thermometer");
        accelerometer = new Accelerometer(this, barometer, thermometer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        accelerometer.startAccelerometerRecording();
        //TextView x = (TextView) findViewById(R.id.hw);
//        x.setText("" + accelerometer.getLastReportTime()); // you need a new thread I think
    }

    @Override
    protected void onPause() {
        super.onPause();
        accelerometer.stopAccelerometer();
    }
}
