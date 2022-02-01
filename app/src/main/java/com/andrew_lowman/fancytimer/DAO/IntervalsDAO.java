package com.andrew_lowman.fancytimer.DAO;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.andrew_lowman.fancytimer.Entities.IntervalsEntity;

import java.util.List;

@Dao
public interface IntervalsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(IntervalsEntity interval);

    @Query("SELECT * FROM intervals_table")
    LiveData<List<IntervalsEntity>> getAllIntervals();

    @Query("DELETE FROM intervals_table WHERE intervalID = :intervalID")
    void deleteInterval(int intervalID);

    @Query("SELECT * FROM intervals_table WHERE intervalID = :intervalID")
    IntervalsEntity getInterval(int intervalID);

    @Query("UPDATE intervals_table SET code = :code WHERE intervalID = :intervalID")
    void update(String code, int intervalID);

    @Query("DELETE FROM intervals_table")
    void deleteAllIntervals();

}
