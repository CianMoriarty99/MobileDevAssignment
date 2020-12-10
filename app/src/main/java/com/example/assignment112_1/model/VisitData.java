package com.example.assignment112_1.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

@Entity
public class VisitData {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name="title")
    private String title;
    private Date dateTime;
    //private PhotoData[] images;
    private List<VisitPoint> points;

    public VisitData(String title, Date dateTime, List<VisitPoint> points) {
        this.title = title;
        this.dateTime = dateTime;
        this.points = points;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public List<VisitPoint> getPoints() {
        return points;
    }
    public void setPoints(List<VisitPoint> points) {
        this.points = points;
    }

    public Date getDateTime() {
        return dateTime;
    }
    public void setDateTime(Date date) {
        this.dateTime = date;
    }
}



