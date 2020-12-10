package com.example.assignment112_1;

import android.media.ExifInterface;
import android.util.Log;

import java.io.IOException;

public class LocationHelper {
    public static LocBool getLocationData(String file) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(file);
            float[] latLong = new float[2];
            Log.d("LAT1", String.valueOf(latLong[0]));
            if (exif.getLatLong(latLong)) {
                Log.d("LAT2", String.valueOf(latLong[0]));
            }
            return new LocBool(latLong, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        float[] latLong = {(float) 0.0};

        return new LocBool(latLong, false);
    }

    public static final class LocBool {
        private final float[] latLong;
        private final Boolean bool;

        public LocBool(float[] first, Boolean second) {
            this.latLong = first;
            this.bool = second;
        }

        public float[] getLatLong() {
            return latLong;
        }

        public Boolean getBool() {
            return bool;
        }
    }
}
