package com.example.assignment112_1.model;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.AndroidViewModel;

import java.io.File;
import java.util.List;

/**
 * This class provides interaction methods between the database (model), app views, and view-models.
 * These are used to store photos as well as visit data in the database.
 */

public class PhotoViewModel extends AndroidViewModel {
    private PhotoRepository mRepository;
    private LiveData<List<PhotoData>> photos;
    private LiveData<List<VisitData>> visits;

    public PhotoViewModel(Application application) {
        super(application);
        mRepository = new PhotoRepository(application);
        photos = mRepository.getPhotoData();
        visits = mRepository.getVisitData();
    }

    public LiveData<List<PhotoData>> getPhotoData() {
        return  photos;
    }

    public LiveData<List<VisitData>> getVisitData() {
        return  visits;
    }

    public void updatePhotoData(PhotoData photoData) {
        mRepository.updatePhotoData(photoData);
    }
    public void insertPhotoData(File photoFile) {
        mRepository.insertPhotoData(photoFile);
    }
    public void insertPhotoData(File photoFile, String title, float[] loc, Float temperature, Float pressure) {
        mRepository.insertPhotoData(photoFile, title, loc, temperature, pressure);
    }
    public void insertVisitData(VisitData visitData) {
        mRepository.insertVisitData(visitData);
    }
}
