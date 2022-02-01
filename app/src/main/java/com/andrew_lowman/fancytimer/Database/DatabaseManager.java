package com.andrew_lowman.fancytimer.Database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.andrew_lowman.fancytimer.DAO.IntervalsDAO;
import com.andrew_lowman.fancytimer.Entities.IntervalsEntity;

@Database(entities = {IntervalsEntity.class}, version = 1)
public abstract class DatabaseManager extends RoomDatabase {
    public abstract IntervalsDAO intervalsDAO();

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
            //new PopulateDB(INSTANCE).execute();
            //new DeleteEntries(INSTANCE).execute();
        }

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            //new DeleteEntries(INSTANCE).execute();

        }
    };

    private static class PopulateDB extends AsyncTask<Void, Void, Void>{
        private final IntervalsDAO mIntervalsDAO;

        public PopulateDB(DatabaseManager db) {

            this.mIntervalsDAO = db.intervalsDAO();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //mIntervalsDAO.deleteAllIntervals();

            //IntervalsEntity ie = new IntervalsEntity("Pomodoro","25,5,25,5,25,5,25,20");
            //mIntervalsDAO.insert(ie);

            return null;
        }
    }

}
