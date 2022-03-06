package com.andrew_lowman.fancytimer.Database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.andrew_lowman.fancytimer.Activity.Report;
import com.andrew_lowman.fancytimer.DAO.IntervalsDAO;
import com.andrew_lowman.fancytimer.DAO.ReportsDAO;
import com.andrew_lowman.fancytimer.Entities.IntervalsEntity;
import com.andrew_lowman.fancytimer.Entities.ReportEntity;

@Database(entities = {IntervalsEntity.class,ReportEntity.class}, version = 3)
public abstract class DatabaseManager extends RoomDatabase {
    public abstract IntervalsDAO intervalsDAO();
    public abstract ReportsDAO reportsDAO();

    private static volatile DatabaseManager INSTANCE;

    public static synchronized DatabaseManager getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (DatabaseManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DatabaseManager.class, "interval_patterns_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(DatabaseManagerCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback DatabaseManagerCallback = new RoomDatabase.Callback(){

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db){
            super.onOpen(db);
            new PopulateDB(INSTANCE).execute();
            //new PopulateDB(INSTANCE).execute();
            //new DeleteEntries(INSTANCE).execute();
        }
    };

    private static class PopulateDB extends AsyncTask<Void, Void, Void>{
        private final IntervalsDAO mIntervalsDAO;
        private final ReportsDAO mReportsDAO;

        public PopulateDB(DatabaseManager db) {

            this.mIntervalsDAO = db.intervalsDAO();
            this.mReportsDAO = db.reportsDAO();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //mIntervalsDAO.deleteAllIntervals();

            //IntervalsEntity ie = new IntervalsEntity("Pomodoro","25,5,25,5,25,5,25,20");
            //mIntervalsDAO.insert(ie);
            //+mReportsDAO.deleteAllReports();
            //ReportEntity re = new ReportEntity(1,"a12/11/22, 1:00, 2:00",3);
            //mReportsDAO.insertReport(re);

            return null;
        }
    }

}
