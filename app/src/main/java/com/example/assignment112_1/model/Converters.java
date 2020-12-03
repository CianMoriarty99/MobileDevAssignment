package com.example.assignment112_1.model;

import androidx.room.TypeConverter;

import org.json.JSONArray;
import org.json.JSONException;

public class Converters {
    @TypeConverter
    public static String FloatArrayToString(float[] values) {
        JSONArray jsonArray = new JSONArray();
        for (float value : values) {
            try {
                jsonArray.put(value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray.toString();
    }

    @TypeConverter
    public static float[] StringToFloatArray(String values) {
        try {
            JSONArray jsonArray = new JSONArray(values);
            float[] floatArray = new float[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                floatArray[i] = Float.parseFloat(jsonArray.getString(i));
            }
            return floatArray;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}

