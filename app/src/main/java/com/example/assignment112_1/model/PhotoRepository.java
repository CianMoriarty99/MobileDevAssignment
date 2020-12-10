package com.example.assignment112_1.model;

import android.app.Application;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.assignment112_1.BitmapHelper;
import com.example.assignment112_1.LocationHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.example.assignment112_1.LocationHelper.getLocationData;

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
    public LiveData<List<VisitData>> getVisitData() {
        return mPhotoDao.retrieveAllVisitData();
    }

    public void updatePhotoData(PhotoData photoData) {
        new updatePhotoAsync(mPhotoDao, photoData, mApplication).execute();
    }

    public void insertPhotoData(File photoFile) {
        new InsertPhotoAsync(mPhotoDao, photoFile, mApplication).execute();
    }
    public void insertPhotoData(File photoFile, String title) {
        new InsertPhotoAsync(mPhotoDao, photoFile, mApplication, title).execute();
    }

    public void insertVisitData(VisitData visitData) {
        new InsertVisitAsync(mPhotoDao, visitData, mApplication).execute();
    }

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
     * Async process to insert photos into the database in the background.
     */
    private static class InsertPhotoAsync extends AsyncTask<Void, Void, Void> {
        private final PhotoDAO mPhotoDao;
        private final File mPhotoFile;
        private final Application mApplication;
        private final String mTitle;

        InsertPhotoAsync(PhotoDAO dao, File photoFile, Application application) {
            mPhotoDao = dao;
            mPhotoFile = photoFile;
            mApplication = application;
            mTitle = null;
        }

        InsertPhotoAsync(PhotoDAO dao, File photoFile, Application application, String title) {
            mPhotoDao = dao;
            mPhotoFile = photoFile;
            mApplication = application;
            mTitle = title;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            try {

                String photoFile = mPhotoFile.toString();
                File thumb = BitmapHelper.generateThumbnail(photoFile, 150, 150, mApplication);
                LocationHelper.LocBool locBool = getLocationData(photoFile);
                float[] loc = locBool.getLatLong();
                Boolean bool = locBool.getBool();
                PhotoData mPhotoData = new PhotoData(photoFile, thumb.toString(), bool, loc, "", mTitle);
                mPhotoDao.insert(mPhotoData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    /**
     * Async process to insert photos into the database in the background.
     */
    private static class InsertVisitAsync extends AsyncTask<Void, Void, Void> {
        private final PhotoDAO mPhotoDao;
        private final VisitData mVisitData;
        private final Application mApplication;

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
