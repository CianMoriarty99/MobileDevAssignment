package com.example.assignment112_1.model;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.assignment112_1.ImageHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This class provides the implementations needed to update visits and photo data in the database.
 */

public class PhotoRepository extends ViewModel {
    private final PhotoDAO mPhotoDao;
    private final  Application mApplication;

    public PhotoRepository(Application application) {
        mApplication = application;
        PhotoDatabase db = PhotoDatabase.getDatabase(mApplication);
        mPhotoDao = db.photoDao();
    }

    /**
     * Gets the image data when it is changed in the database and returns it to the ViewModel.
     */
    public LiveData<List<PhotoData>> getPhotoData() {
        return mPhotoDao.retrieveAllData();
    }

    /**
     * Gets the visit data when it is changed in the database and returns it to the ViewModel.
     */
    public LiveData<List<VisitData>> getVisitData() {
        return mPhotoDao.retrieveAllVisitData();
    }

    /**
     * Updates photo data in the database.
     */
    public void updatePhotoData(PhotoData photoData) {
        new updatePhotoAsync(mPhotoDao, photoData, mApplication).execute();
    }

    /**
     * Inserts a new photo taken from the home screen into the database.
     */
    public void insertPhotoData(File photoFile) {
        new InsertPhotoAsync(mPhotoDao, photoFile, mApplication).execute();
    }

    /**
     * Inserts a new photo taken during a visit into the database along with its associated
     * metadata.
     */
    public void insertPhotoData(File photoFile, String title, float[] loc, Float temperature, Float pressure) {
        new InsertPhotoAsync(mPhotoDao, photoFile, mApplication, title, loc, temperature, pressure).execute();
    }

    /**
     * Inserts a new visit into the database, which includes all the location, temperature, and
     * pressure data points tracked during the visit.
     */
    public void insertVisitData(VisitData visitData) {
        new InsertVisitAsync(mPhotoDao, visitData, mApplication).execute();
    }

    /**
     * This class provides async process to update photos into the database in the background.
     */
    private static class updatePhotoAsync extends AsyncTask<Void, Void, Void> {
        private final PhotoDAO mPhotoDao;
        private final PhotoData mPhotoData;
        private final  Application mApplication;

        updatePhotoAsync(PhotoDAO dao, PhotoData photoData, Application application) {
            mPhotoDao = dao;
            mPhotoData = photoData;
            mApplication = application;
        }


        @Override
        protected Void doInBackground(final Void... params) {
            mPhotoDao.update(mPhotoData);
            return null;
        }
    }

    /**
     * This class provides async process to insert photos into the database in the background.
     */
    private static class InsertPhotoAsync extends AsyncTask<Void, Void, Void> {
        private final PhotoDAO mPhotoDao;
        private final File mPhotoFile;
        private final Application mApplication;
        private final String mTitle;
        private final float[] mLoc;
        private final Float mPressure;
        private final Float mTemperature;

        /**
         * Inserts a photo taken from the home screen outside of a recorded visit into the database.
         */
        InsertPhotoAsync(PhotoDAO dao, File photoFile, Application application) {
            mPhotoDao = dao;
            mPhotoFile = photoFile;
            mApplication = application;
            mTitle  = null;
            mLoc = new float[]{};
            mPressure = null;
            mTemperature = null;
        }

        /**
         * Inserts a photo taken during a recorded visit into the database along with its associated
         * metadata.
         */
        InsertPhotoAsync(PhotoDAO dao, File photoFile, Application application, String title, float[] loc, Float temp, Float pressure) {
            mPhotoDao = dao;
            mPhotoFile = photoFile;
            mApplication = application;
            mTitle = title;
            mLoc = loc;
            mTemperature = temp;
            mPressure = pressure;

        }

        @Override
        protected Void doInBackground(final Void... params) {

            String photoFile = mPhotoFile.getAbsolutePath();
            String thumb = ImageHelper.getThumbFile(photoFile, 150, 150, mApplication);
            PhotoData mPhotoData = new PhotoData(photoFile, thumb, mLoc, "", mTitle, mTemperature, mPressure);
            mPhotoDao.insert(mPhotoData);
            return null;
        }
    }


    /**
     * This class provides sync process to insert visits into the database in the background.
     */
    private static class InsertVisitAsync extends AsyncTask<Void, Void, Void> {
        private final PhotoDAO mPhotoDao;
        private final VisitData mVisitData;
        private final Application mApplication;

        /**
         * Inserts location and sensor data taken during a recorded visit into the database.
         */
        InsertVisitAsync(PhotoDAO dao, VisitData visitData, Application application) {
            mPhotoDao = dao;
            mVisitData = visitData;
            mApplication = application;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            mPhotoDao.insert(mVisitData);
            return null;
        }
    }
}
