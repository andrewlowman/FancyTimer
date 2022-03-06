package com.andrew_lowman.fancytimer.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.andrew_lowman.fancytimer.R;
import com.andrew_lowman.fancytimer.ui.ReportDetailsAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReportDetails extends AppCompatActivity {
    private String name;
    private int id;
    private String code;
    private int fkID;
    private int timesRun;

    private List<String> codeArray = new ArrayList<>();

    private TextView nameTextView;
    private TextView idTextView;
    private TextView timesRunTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        name = getIntent().getStringExtra("name");
        code= getIntent().getStringExtra("code");
        fkID = getIntent().getIntExtra("fkID",0);
        timesRun = getIntent().getIntExtra("timesRun",0);
        id = getIntent().getIntExtra("id",0);

        nameTextView = findViewById(R.id.reportDetailsNameTextView);
        idTextView = findViewById(R.id.reportDetailsIntervalIDTextView);
        timesRunTextView = findViewById(R.id.reportDetailsTimesRunTextView);

        String textID = "ID No: " + fkID;
        String textTimesRun = "Times Run: " + timesRun;
        String textName = "Interval Name:  " + name;
        idTextView.setText(textID);
        timesRunTextView.setText(textTimesRun);
        nameTextView.setText(textName);

        ReportDetailsAdapter reportDetailsAdapter = new ReportDetailsAdapter(this);
        RecyclerView recyclerView = findViewById(R.id.reportDetailsRecyclerView);
        recyclerView.setAdapter(reportDetailsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(code != null){
            String[] details = code.split(",");
            for(int i = 0; i<details.length;i++){
                codeArray.add(details[i]);
            }
        }

        reportDetailsAdapter.setReports(codeArray);
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
}