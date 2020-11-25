package com.example.assignment112_1.model;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.AndroidViewModel;

import com.example.assignment112_1.BitmapHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PhotoViewModel extends AndroidViewModel {
    private PhotoRepository mRepository;
    private LiveData<List<PhotoData>> photos;

    public PhotoViewModel(Application application) {
        super(application);
        mRepository = new PhotoRepository(application);
        photos = mRepository.getPhotoData();
    }

    public LiveData<List<PhotoData>> getPhotoData() {
        return  photos;
    }


    public void insertPhotoData(PhotoData photoData) {
        mRepository.InsertPhotoData(photoData);
    }
}
