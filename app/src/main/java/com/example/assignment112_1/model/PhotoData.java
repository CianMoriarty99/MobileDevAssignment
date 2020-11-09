package com.example.assignment112_1.model;

import android.graphics.Bitmap;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.File;

//@Entity(indices={@Index(value={"title"})})
@Entity
public class PhotoData {
    @PrimaryKey(autoGenerate = true) private int id=0;
    private String file;

    @Ignore
    public Bitmap picture;

    public PhotoData(String file) {
        this.file = file;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getFile() {
        return file;
    }
    public void setFile(String file) {
        this.file = file;
    }

    public Bitmap getPicture() {
        return picture;
    }
    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }
}
