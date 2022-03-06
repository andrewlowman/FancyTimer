package com.andrew_lowman.fancytimer.Database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.andrew_lowman.fancytimer.DAO.IntervalsDAO;
import com.andrew_lowman.fancytimer.DAO.ReportsDAO;
import com.andrew_lowman.fancytimer.Entities.IntervalsEntity;
import com.andrew_lowman.fancytimer.Entities.ReportEntity;

import java.util.List;

public class Repository {
    private IntervalsDAO mIntervalsDAO;
    private ReportsDAO mReportsDAO;

    private LiveData<List<IntervalsEntity>> mAllIntervals;
    private LiveData<List<ReportEntity>> mAllReports;

    public Repository(Application application){
        DatabaseManager db = DatabaseManager.getDatabase(application);
        mIntervalsDAO = db.intervalsDAO();
        mReportsDAO = db.reportsDAO();

        mAllIntervals = mIntervalsDAO.getAllIntervals();
        mAllReports = mReportsDAO.getAllReports();
    }

    public LiveData<List<IntervalsEntity>> getAllIntervals() {
        return mAllIntervals;
    }
    public LiveData<List<ReportEntity>> getAllReports(){return mAllReports;}

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

    public void insertReport(ReportEntity reportEntity){
        new insertAsyncReport(mReportsDAO).execute(reportEntity);
    }

    private static class insertAsyncReport extends AsyncTask<ReportEntity, Void, Void>{
        private ReportsDAO asyncDao;

        insertAsyncReport(ReportsDAO dao){
            asyncDao = dao;
        }

        @Override
        protected Void doInBackground(ReportEntity... reportEntities) {
            asyncDao.insertReport(reportEntities[0]);
            return null;
        }
    }

    public void updateReport(int id, String code){
        new updateReportAsync(mReportsDAO).execute(id,code);
    }

    private static class updateReportAsync extends AsyncTask<Object, Void, Void>{
        private ReportsDAO asyncReportDAO;

        public updateReportAsync(ReportsDAO dao) {
            asyncReportDAO = dao;
        }

        @Override
        protected Void doInBackground(Object... objects) {
            Integer id = (Integer) objects[0];
            String code = (String) objects[1];

            asyncReportDAO.updateReport(id,code);
            return null;
        }
    }

    public void deleteReport(int reportID){
        new deleteAsyncReport(mReportsDAO).execute(reportID);
    }

    private static class deleteAsyncReport extends AsyncTask<Integer, Void, Void>{
        private ReportsDAO reportsDAO;

        deleteAsyncReport(ReportsDAO dao){
            reportsDAO = dao;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            reportsDAO.deleteReport(integers[0]);
            return null;
        }
    }

    public void updateTimesRun(int reportID, int timesRun){
        new updateTimesAsync(mReportsDAO).execute(reportID,timesRun);
    }

    private static class updateTimesAsync extends AsyncTask<Object,Void,Void>{
        private ReportsDAO asyncReportDAO;

        public updateTimesAsync(ReportsDAO dao){
            asyncReportDAO = dao;
        }

        @Override
        protected Void doInBackground(Object... objects) {
            Integer id = (Integer) objects[0];
            Integer timesRun = (Integer) objects[1];
            asyncReportDAO.updateTimesRun(id,timesRun);
            return null;
        }
    }

    /*public ReportEntity getReport(int id){
        new getReportAsync(mReportsDAO).execute(id);
    }

    private static class getReportAsync extends AsyncTask<ReportEntity, Void, Void>{
        private ReportsDAO asyncReportsDAO;

        public getReportAsync(ReportsDAO dao){
            asyncReportsDAO = dao;
        }

        @Override
        protected Void doInBackground(ReportEntity... reportEntities) {
            return null;
        }
    }*/
}
