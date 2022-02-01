package com.andrew_lowman.fancytimer.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.andrew_lowman.fancytimer.Entities.IntervalsEntity;
import com.andrew_lowman.fancytimer.R;
import com.andrew_lowman.fancytimer.ViewModel.IntervalsPlannerSwipeToDelete;
import com.andrew_lowman.fancytimer.ViewModel.IntervalsViewModel;
import com.andrew_lowman.fancytimer.ViewModel.SwipeToDelete;
import com.andrew_lowman.fancytimer.ui.IntervalsPlannerAdapter;

import java.io.Serializable;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IntervalsPlanner extends AppCompatActivity {
    private Button addButton;
    private Button saveButton;
    private Button cancelButton;
    private Button untimedButton;
    private EditText nameEditText;

    private NumberPicker minsNP;
    private NumberPicker secsNP;

    private List<String> times;
    private List<Long> timesToSend;
    private List<IntervalsEntity> storedIntervals;

    private IntervalsPlannerAdapter intervalsPlannerAdapter;

    private IntervalsViewModel intervalsViewModel;

    private int idNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intervals_planner);

        addButton = findViewById(R.id.intervalsPlannerAddButton);
        saveButton = findViewById(R.id.intervalsPlannerSaveButton);
        cancelButton = findViewById(R.id.intervalsPlannerCancelButton);
        untimedButton = findViewById(R.id.intervalsPlannerUntimedButton);
        nameEditText = findViewById(R.id.intervalsPlannerEditText);

        minsNP = findViewById(R.id.intervalsMinutesNumberPicker);
        secsNP = findViewById(R.id.intervalsSecondsNumberPicker);

        minsNP.setMaxValue(59);
        minsNP.setMinValue(0);

        secsNP.setMaxValue(59);
        secsNP.setMinValue(0);

        times = new ArrayList<>();
        timesToSend = new ArrayList<>();
        storedIntervals = new ArrayList<>();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        intervalsViewModel = new ViewModelProvider(this).get(IntervalsViewModel.class);

        RecyclerView recyclerView = findViewById(R.id.intervalsPlannerRecyclerView);
        intervalsPlannerAdapter = new IntervalsPlannerAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(intervalsPlannerAdapter);
        intervalsPlannerAdapter.setTimes(times);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new IntervalsPlannerSwipeToDelete(intervalsPlannerAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(secsNP.getValue() == 0 && minsNP.getValue() == 0){

                }else{
                    timesToSend.add(convertToLong());
                    times.add(convertToTime());
                    intervalsPlannerAdapter.notifyDataSetChanged();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title;
                if(nameEditText.getText().toString().isEmpty()){
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm", Locale.getDefault());
                    title = sdf.format(new Date());
                }else{
                    title = nameEditText.getText().toString();
                }
                IntervalsEntity ie = new IntervalsEntity(title,converToString(timesToSend));
                intervalsViewModel.insert(ie);
                Intent intent = new Intent(IntervalsPlanner.this,Intervals.class);
                intent.putExtra("StringTimesArray", (Serializable) times);
                intent.putExtra("LongTimesArray", (Serializable) timesToSend);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });

        untimedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timesToSend.add(0L);
                times.add("Stopwatch");
                intervalsPlannerAdapter.notifyDataSetChanged();
            }
        });

    }

    public long convertToLong(){
        return (minsNP.getValue() * 60000L) + (secsNP.getValue() * 1000L);
    }

    public String convertToTime(){
        return String.format(Locale.getDefault(),"%02d:%02d",minsNP.getValue(),secsNP.getValue());
    }

    public String converToString(List<Long> list){
        String toDB = "";
        for(long l:list){
            toDB += l + ",";
        }
        return toDB;
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