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

/**
 * This class provides the necessary information to create the photos table in the database. It also
 * provides the necessary setter and getter methods.
 */

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
    private float[] loc;
    private String description;
    @ColumnInfo(name="path_title")
    private String pathTitle;
    private Float pressure;
    private Float temperature;

    @Ignore
    public Bitmap picture;

    public PhotoData(String photoFile, String thumbFile, float[] loc,
                     String description, String pathTitle, Float temperature, Float pressure) {
        this.photoFile = photoFile;
        this.thumbFile = thumbFile;
        this.loc = loc;
        this.description = description;
        this.pathTitle = pathTitle;
        this.temperature = temperature;
        this.pressure = pressure;
    }


    protected PhotoData(Parcel in) {
        id = in.readInt();
        photoFile = in.readString();
        thumbFile = in.readString();
        loc = in.createFloatArray();
        description = in.readString();
        pathTitle = in.readString();
        if (in.readByte() == 0) {
            pressure = null;
        } else {
            pressure = in.readFloat();
        }
        if (in.readByte() == 0) {
            temperature = null;
        } else {
            temperature = in.readFloat();
        }
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

    public Float getTemperature() {
        return temperature;
    }
    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public Float getPressure() {
        return pressure;
    }
    public void setPressure(Float pressure) {
        this.pressure = pressure;
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
        parcel.writeFloatArray(loc);
        parcel.writeString(description);
        parcel.writeString(pathTitle);
        if (pressure == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeFloat(pressure);
        }
        if (temperature == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeFloat(temperature);
        }
        parcel.writeParcelable(picture, i);
    }
}
