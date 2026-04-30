package com.trackify.app.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LocationDao {

    @Query("SELECT * FROM saved_locations ORDER BY id DESC")
    List<SavedLocation> getAll();

    @Query("SELECT * FROM saved_locations ORDER BY id DESC")
    LiveData<List<SavedLocation>> getAllLive();

    @Insert
    long insert(SavedLocation location);

    @Delete
    void delete(SavedLocation location);
}
