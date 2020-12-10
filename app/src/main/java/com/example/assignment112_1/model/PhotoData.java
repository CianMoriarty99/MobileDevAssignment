package com.example.assignment112_1.model;

import android.graphics.Bitmap;
import android.icu.text.CaseMap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.assignment112_1.TrackingActivity;

import java.io.File;

@Entity(foreignKeys = {
@ForeignKey(entity = VisitData.class,
        parentColumns = "title",
        childColumns = "path_title",
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE)})
public class PhotoData implements Parcelable {
    @PrimaryKey(autoGenerate = true) private int id=0;
    private String photoFile;
    private String thumbFile;
    private Boolean hasLoc;
    private float[] loc;
    private String description;
    @ColumnInfo(name="path_title")
    private String pathTitle;

    @Ignore
    public Bitmap picture;

    public PhotoData(String photoFile, String thumbFile, Boolean hasLoc, float[] loc, String description, String pathTitle) {
        this.photoFile = photoFile;
        this.thumbFile = thumbFile;
        this.loc = loc;
        this.hasLoc = hasLoc;
        this.description = description;
        this.pathTitle = pathTitle;
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

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public float[] getLoc() {
        return loc;
    }
    public void setLoc(float[] loc) {
        this.loc = loc;
    }

    public Boolean getHasLoc() {
        return hasLoc;
    }
    public void setHasLoc(Boolean hasLoc) {
        this.hasLoc = hasLoc;
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

    public String getPathTitle() {
        return pathTitle;
    }
    public void setPathTitle(String title) {
        this.pathTitle = title;
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
