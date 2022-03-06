package com.andrew_lowman.fancytimer.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.andrew_lowman.fancytimer.Database.Repository;
import com.andrew_lowman.fancytimer.Entities.ReportEntity;

import java.util.List;

public class ReportsViewModel extends AndroidViewModel {
    private Repository mRepository;
    private LiveData<List<ReportEntity>> mAllReports;

    public ReportsViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
        mAllReports = mRepository.getAllReports();
    }

    public LiveData<List<ReportEntity>> getAllReports() {
        return mAllReports;
    }

    public void insertReport(ReportEntity reportEntity){
        mRepository.insertReport(reportEntity);
    }

    public void deleteReport(int reportID){
        mRepository.deleteReport(reportID);
    }

    public void updateReport(int index, String code){
        mRepository.updateReport(index,code);
    }

    public void updateTimesRun(int index,int timesRun){mRepository.updateTimesRun(index,timesRun);}
}
