package com.example.assignment112_1.model;


import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

/**
 * This class provides functions to convert between data types as needed for storage in the
 * database. The available conversions are as follows:
 * between [database-friendly format] and [convenient format]
 * - between String and Float[]
 * - between String and VisitPoint
 * - between Long and Date
 */

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

    @TypeConverter
    public String fromVisitPointList(List<VisitPoint> visitPoints) {
        if (visitPoints == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<VisitPoint>>() {}.getType();
        String json = gson.toJson(visitPoints, type);
        return json;
    }

    @TypeConverter
    public List<VisitPoint> toVisitPointList(String string) {
        if (string == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<VisitPoint>>() {}.getType();
        return gson.fromJson(string, type);
    }


    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}

