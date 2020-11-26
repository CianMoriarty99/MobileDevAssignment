package com.example.assignment112_1.model;

import android.graphics.Bitmap;
import android.icu.text.CaseMap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.File;

@Entity
public class PhotoData implements Parcelable {
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


    protected PhotoData(Parcel in) {
        id = in.readInt();
        photoFile = in.readString();
        thumbFile = in.readString();
        picture = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<PhotoData> CREATOR = new Creator<PhotoData>() {
        @Override
        public PhotoData createFromParcel(Parcel in) {
            return new PhotoData(in);
        }

        @Override
        public PhotoData[] newArray(int size) {
            return new PhotoData[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(photoFile);
        parcel.writeString(thumbFile);
        parcel.writeParcelable(picture, i);
    }
}
