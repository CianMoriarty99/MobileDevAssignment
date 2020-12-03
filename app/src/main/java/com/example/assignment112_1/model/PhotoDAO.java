package com.example.assignment112_1.model;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PhotoDAO {
    @Insert
    void insertAll(PhotoData... photodata);

    @Insert
    void insert(PhotoData photodata);

    @Update
    public void update(PhotoData photoData);

    @Delete
    void delete (PhotoData photoData);

    @Query("SELECT * FROM PhotoData ORDER BY id ASC")
    LiveData<List<PhotoData>> retrieveAllData();


    @Delete
    void deleteAll(PhotoData...photoData);
}

