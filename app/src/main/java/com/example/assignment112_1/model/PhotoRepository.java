package com.example.assignment112_1.model;

import android.app.Application;
import android.media.ExifInterface;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.assignment112_1.BitmapHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;

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


    public void updatePhotoData(PhotoData photoData) {

    }


    public void insertPhotoData(File photoFile) {
        new InsertIntoDbAsync(mPhotoDao, photoFile, mApplication).execute();
    }



    private static class updateDbAsync extends AsyncTask<Void, Void, Void> {
        private final PhotoDAO mPhotoDao;
        private final PhotoData mPhotoData;
        private final  Application mApplication;

        updateDbAsync(PhotoDAO dao, PhotoData photoData, Application application) {
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
    private static class InsertIntoDbAsync extends AsyncTask<Void, Void, Void> {
        private final PhotoDAO mPhotoDao;
        private final File mPhotoFile;
        private final Application mApplication;

        InsertIntoDbAsync(PhotoDAO dao, File photoFile, Application application) {
            mPhotoDao = dao;
            mPhotoFile = photoFile;
            mApplication = application;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            try {

                String photoFile = mPhotoFile.toString();
                File thumb = BitmapHelper.generateThumbnail(photoFile, 150, 150, mApplication);
                LocBool locBool = getLocationData(photoFile);
                float[] loc = locBool.latLong;
                Boolean bool = locBool.bool;
                PhotoData mPhotoData = new PhotoData(photoFile, thumb.toString(), bool, loc, "");
                mPhotoDao.insert(mPhotoData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static LocBool getLocationData(String file) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(file);
            float[] latLong = new float[2];
            boolean hasLatLong = exif.getLatLong(latLong);
            if (hasLatLong) {
                System.out.println("Latitude: " + latLong[0]);
                System.out.println("Longitude: " + latLong[1]);
            }
            return new LocBool(latLong, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        float[] latLong = {(float) 0.0};

        return new LocBool(latLong, false);
    }

    static final class LocBool {
        private final float[] latLong;
        private final Boolean bool;

        public LocBool(float[] first, Boolean second) {
            this.latLong = first;
            this.bool = second;
        }
    }

}
