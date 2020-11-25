package com.example.assignment112_1.model;

import android.graphics.Bitmap;
import android.icu.text.CaseMap;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.File;

@Entity
public class PhotoData {
    @PrimaryKey(autoGenerate = true) private int id=0;
    private String photoFile;
    private String thumbFile;
   // private String description;
   // private String title;
   // private String location;

    @Ignore
    public Bitmap picture;

    public PhotoData(String photoFile, String thumbFile) {
        this.photoFile = photoFile;
        this.thumbFile = thumbFile;
    }


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getPhotoFile() {
        return photoFile;
    }
    public void setPhotoFile(String file) {
        this.photoFile = file;
    }

    public String getThumbFile() {
        return thumbFile;
    }
    public void setThumbFile(String file) {
        this.thumbFile = file;
    }

    public Bitmap getPicture() {
        return picture;
    }
    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }
}
