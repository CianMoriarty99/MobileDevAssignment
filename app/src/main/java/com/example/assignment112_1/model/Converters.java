package com.example.assignment112_1.model;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Converters {
    @TypeConverter
    public static String FloatArrayToString(Float[] values) {
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
    public static Float[] StringToFloatArray(String values) {
        try {
            JSONArray jsonArray = new JSONArray(values);
            Float[] floatArray = new Float[jsonArray.length()];
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

