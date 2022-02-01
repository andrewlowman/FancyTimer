package com.andrew_lowman.fancytimer.Database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.andrew_lowman.fancytimer.DAO.IntervalsDAO;
import com.andrew_lowman.fancytimer.Entities.IntervalsEntity;

import java.util.List;

public class Repository {
    private IntervalsDAO mIntervalsDAO;

    private LiveData<List<IntervalsEntity>> mAllIntervals;

    public Repository(Application application){
        DatabaseManager db = DatabaseManager.getDatabase(application);
        mIntervalsDAO = db.intervalsDAO();

        mAllIntervals = mIntervalsDAO.getAllIntervals();
    }

    public LiveData<List<IntervalsEntity>> getAllIntervals() {
        return mAllIntervals;
    }

    public void insert(IntervalsEntity intervalsEntity){
        new insertAsyncInterval(mIntervalsDAO).execute(intervalsEntity);
    }

    private static class insertAsyncInterval extends AsyncTask<IntervalsEntity, Void, Void>{
        private IntervalsDAO asyncDao;

        insertAsyncInterval(IntervalsDAO dao){
            asyncDao = dao;
        }

        @Override
        protected Void doInBackground(IntervalsEntity... intervalsEntities) {
            asyncDao.insert(intervalsEntities[0]);
            return null;
        }
    }

    public void delete(int intervalID){
        new deleteAsyncInterval(mIntervalsDAO).execute(intervalID);
    }

    private static class deleteAsyncInterval extends AsyncTask<Integer, Void, Void>{
        private IntervalsDAO asyncIntervalDAO;

        deleteAsyncInterval(IntervalsDAO dao){
            asyncIntervalDAO = dao;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            asyncIntervalDAO.deleteInterval(integers[0]);
            return null;
        }
    }

    public void insertInPosition(int position, IntervalsEntity intervalsEntity){
        new insertAsyncIntervalInPosition(mIntervalsDAO).execute(intervalsEntity);
    }

    private static class insertAsyncIntervalInPosition extends AsyncTask<IntervalsEntity, Void, Void>{
        private IntervalsDAO asyncDao;

        insertAsyncIntervalInPosition(IntervalsDAO dao){
            asyncDao = dao;
        }

        @Override
        protected Void doInBackground(IntervalsEntity... intervalsEntities) {
            asyncDao.insert(intervalsEntities[0]);
            return null;
        }
    }
}
