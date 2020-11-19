package com.example.assignment112_1.model;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.assignment112_1.BitmapHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class PhotoRepository extends ViewModel {
    private final PhotoDAO mPhotoDao;
    private final  Application mApplication;

    public PhotoRepository(Application application) {
        mApplication = application;
        PhotoDatabase db = PhotoDatabase.getDatabase(mApplication);
        mPhotoDao = db.photoDao();
    }

    /**
     * it gets the data when changed in the db and returns it to the ViewModel
     * @return
     */
    public LiveData<List<PhotoData>> getPhotoData() {
        return mPhotoDao.retrieveAllData();
    }


    public void InsertPhotoData(PhotoData photoData) {
        new InsertIntoDbAsync(mPhotoDao, photoData, mApplication).execute();
    }


    /**
     * Async process to insert photos into the database in the background.
     */
    private static class InsertIntoDbAsync extends AsyncTask<Void, Void, Void> {
        private final PhotoDAO mPhotoDao;
        private final PhotoData mPhotoData;
        private final  Application mApplication;

        InsertIntoDbAsync(PhotoDAO dao, PhotoData photoData, Application application) {
            mPhotoDao = dao;
            mPhotoData= photoData;
            mApplication = application;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            try {
                File thumb = BitmapHelper.generateThumbnail(mPhotoData.getPhotoFile(), 150, 150, mApplication);
                mPhotoData.setThumbFile(thumb.toString());
                mPhotoDao.insert(mPhotoData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    /*    @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mPresenter.titleDescriptionInserted(mPhotoData.getTitle(), mPhotoData.getDescription());
        }

     */
    }

}
