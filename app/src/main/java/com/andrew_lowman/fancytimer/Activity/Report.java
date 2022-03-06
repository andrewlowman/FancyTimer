package com.andrew_lowman.fancytimer.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.andrew_lowman.fancytimer.Entities.IntervalsEntity;
import com.andrew_lowman.fancytimer.Entities.ReportEntity;
import com.andrew_lowman.fancytimer.R;
import com.andrew_lowman.fancytimer.ViewModel.IntervalsViewModel;
import com.andrew_lowman.fancytimer.ViewModel.ReportsSwipeToDelete;
import com.andrew_lowman.fancytimer.ViewModel.ReportsViewModel;
import com.andrew_lowman.fancytimer.ui.ReportsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Report extends AppCompatActivity {
    //private IntervalsViewModel intervalsViewModel;

    private int intervalID;
    private int reportID;

    private List<ReportEntity> reports = new ArrayList<>();
    private List<String> sentReportDetails;
    private List<IntervalsEntity> intervals;
    private ReportEntity theReport;
    private String code;

    private Button searchButton;
    private EditText searchBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        sentReportDetails = new ArrayList<>();
        intervals = new ArrayList<>();

        searchButton = findViewById(R.id.loadReportSearchButton);
        searchBox = findViewById(R.id.loadReportSearchBox);

        ReportsViewModel reportsViewModel = new ViewModelProvider(this).get(ReportsViewModel.class);
        IntervalsViewModel intervalsViewModel = new ViewModelProvider(this).get(IntervalsViewModel.class);
        //intervalsViewModel = new ViewModelProvider(this).get(IntervalsViewModel.class);
        RecyclerView recyclerView = findViewById(R.id.reportRecyclerView);
        final ReportsAdapter reportsAdapter = new ReportsAdapter(this,reportsViewModel);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(reportsAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ReportsSwipeToDelete(reportsAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        reportID = getIntent().getIntExtra("reportID",0);
        intervalID = getIntent().getIntExtra("intervalID",0);

        reportsViewModel.getAllReports().observe(this, new Observer<List<ReportEntity>>() {
                    @Override
                    public void onChanged(@Nullable final List<ReportEntity> reportEntities) {
                        reportsAdapter.setReports(reportEntities);
                        reports.addAll(reportEntities);
                        reportsAdapter.notifyDataSetChanged();
                    }
                });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = searchBox.getText().toString();
                reportsAdapter.setReports(searchReports(text,reports));
                reportsAdapter.notifyDataSetChanged();
            }
        });

        /*intervalsViewModel.getAllIntervals().observe(this, new Observer<List<IntervalsEntity>>() {
            @Override
            public void onChanged(List<IntervalsEntity> intervalsEntities) {
                intervals.addAll(intervalsEntities);
            }
        });*/

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for(ReportEntity re:reports){
                    if(re.getFkIntervalID() == intervalID){
                        code = re.getReportCode();
                        theReport = re;
                    }
                }
            }
        },500);*/

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //split string at commas
                if(code != null){
                    String[] strings = code.split(",");
                    List<String> reportDetails = new ArrayList<>();
                    reportDetails.add("Name: " + theReport.getReportName());
                    reportDetails.add("Number of times run: " + theReport.getNumberOfTimesRun());
                    for(int i = 0;i<strings.length;i++){
                        reportDetails.add(strings[i]);
                    }
                    reportsAdapter.setReports(reportDetails);
                }
            }
        },1000);*/
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public List<ReportEntity> searchReports(String text,List<ReportEntity> reportsList){
        //check if searchbox is empty -whole list
        if(text.isEmpty()){
            return reportsList;
        }
        //new list to fill
        List<ReportEntity> newList = new ArrayList<>();
        //thru list and check if name or id contains search term
        for(ReportEntity re:reportsList){
            if(re.getIntervalName().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))||Integer.toString(re.getReportID()).toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))){
                newList.add(re);
            }
        }
        return newList;
    }
}