package com.andrew_lowman.fancytimer.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.andrew_lowman.fancytimer.Entities.ReportEntity;

import java.util.List;

@Dao
public interface ReportsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertReport(ReportEntity report);

    @Query("SELECT * FROM reports_table")
    LiveData<List<ReportEntity>> getAllReports();

    @Query("UPDATE reports_table SET reportCode = :code WHERE reportID = :reportID")
    void updateReport(int reportID, String code);

    @Query("UPDATE reports_table SET numberOfTimesRun = :times WHERE reportID =:reportID")
    void updateTimesRun(int reportID,int times);

    @Query("DELETE FROM reports_table WHERE reportID = :reportID")
    void deleteReport(int reportID);

    @Query("DELETE FROM reports_table")
    void deleteAllReports();
}