package com.example.assignment112_1.model;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

public class PhotoViewModel extends AndroidViewModel {
    private final PhotoDAO mPhotoDao;
    private LiveData<List<PhotoData>> photos;

    public PhotoViewModel(Application application) {
        super(application);
        PhotoDatabase db = PhotoDatabase.getDatabase(application);
        mPhotoDao = db.photoDao();
    }



    public LiveData<List<PhotoData>> getPhotoData() {
        if (photos == null) {
            photos = mPhotoDao.retrieveAllData();
        }
        return photos;
    }


    public void insertPhotoData(PhotoData photoData) {
        new InsertIntoDbAsync(mPhotoDao, photoData).execute();
    }


    /**
     * Async process to insert photos into the database in the background.
     */
    private static class InsertIntoDbAsync extends AsyncTask<Void, Void, Void> {
        private final PhotoDAO mPhotoDao;
        private final PhotoData mPhotoData;

        InsertIntoDbAsync(PhotoDAO dao, PhotoData photoData) {
            mPhotoDao = dao;
            mPhotoData= photoData;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            mPhotoDao.insert(mPhotoData);
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
