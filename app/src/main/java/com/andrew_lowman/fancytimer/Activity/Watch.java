package com.andrew_lowman.fancytimer.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.andrew_lowman.fancytimer.Model.Clock;
import com.andrew_lowman.fancytimer.R;
import com.andrew_lowman.fancytimer.ui.WatchAdapter;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Watch extends AppCompatActivity implements Clock {

    private Button startButton;
    private Button intervalButton;
    private Button cancelButton;
    private TextView timerTextView;
    private TextView intervalTextView;

    private boolean running = false;
    private boolean intervalRunning = false;
    //private boolean intervalStarted = true;
    private boolean intervalTextViewVisible = false;

    //first timer
    private long milliseconds;
    private long startTime;
    private long updateTime;
    private long currentTime;
    final Handler mainHandler = new Handler();

    //second timer
    private long secondMilliseconds;
    private long secondStartTime;
    private long secondUpdateTime;
    private long secondCurrentTime;
    final Handler secondHandler = new Handler();

    private List<String> times;
    private WatchAdapter watchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);

        times = new ArrayList<>();
        startButton = findViewById(R.id.startButton);
        intervalButton = findViewById(R.id.intervalButton);
        cancelButton = findViewById(R.id.cancelButton);

        NavigationBarView navigationView = findViewById(R.id.bottomNavigation);
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch(item.getItemId()){
                    case R.id.timer_menu_item:
                        intent =  new Intent(Watch.this,Timer.class);
                        startActivity(intent);
                        return true;
                    case R.id.intervals_menu_item:
                        intent = new Intent(Watch.this,Intervals.class);
                        startActivity(intent);
                        return true;
                    case R.id.stopwatch_menu_item:
                        return true;
                }
                return false;
            }
        });

        timerTextView = findViewById(R.id.timerTextView);
        intervalTextView = findViewById(R.id.intervalTextView);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        watchAdapter = new WatchAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(watchAdapter);
        watchAdapter.setTimes(times);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!running){
                    startButton.setText("Pause");
                    start();
                    running = true;
                }else{
                    startButton.setText("Start");
                    pause();
                    running = false;
                }
            }
        });

        intervalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(running){
                    if(!intervalTextViewVisible) {
                        intervalTextView.setVisibility(View.VISIBLE);
                        intervalTextViewVisible = true;
                    }
                    interval();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    @Override
    public void start() {
        startTime = SystemClock.elapsedRealtime();
        mainHandler.postDelayed(mainRunnable,0);
        currentTime += milliseconds;
        if(intervalRunning){
            secondStartTime = SystemClock.elapsedRealtime();
            secondHandler.postDelayed(secondRunnable,0);
            secondCurrentTime += secondMilliseconds;
        }
    }

    @Override
    public void start(long timer) {

    }

    @Override
    public void pause() {
        mainHandler.removeCallbacks(mainRunnable);
        if(intervalRunning){
            secondHandler.removeCallbacks(secondRunnable);
        }
    }

    @Override
    public void cancel() {
        pause();
        timerTextView.setText("00:00:000");
        intervalTextView.setText("00:00:000");
        intervalTextView.setVisibility(View.INVISIBLE);
        milliseconds = 0L;
        startTime = 0L;
        updateTime = 0L;
        currentTime = 0L;
        secondMilliseconds = 0L;
        secondStartTime = 0L;
        secondUpdateTime = 0L;
        secondCurrentTime = 0L;
        startButton.setText("Start");
        running = false;
        intervalRunning = false;
        intervalTextViewVisible = false;
        times.clear();
        watchAdapter.notifyDataSetChanged();
    }

    public void interval(){
        secondStartTime = SystemClock.elapsedRealtime();
        secondHandler.postDelayed(secondRunnable,0);
        if(intervalRunning){
            times.add(intervalTextView.getText().toString());
        }else{
            times.add(timerTextView.getText().toString());
        }
        watchAdapter.notifyDataSetChanged();
        intervalRunning = true;
    }

    Runnable mainRunnable = new Runnable() {
        @Override
        public void run() {
            milliseconds = SystemClock.elapsedRealtime() - startTime;
            updateTime = milliseconds + currentTime;

            int secs = (int) updateTime / 1000;
            int minutes = secs / 60;
            secs = secs % 60;
            int mils = (int) updateTime % 1000;

            String time = String.format(Locale.getDefault(), "%02d:%02d:%03d", minutes, secs, mils);

            timerTextView.setText(time);

            mainHandler.postDelayed(this, 0);
        }
    };

    Runnable secondRunnable = new Runnable() {
        @Override
        public void run() {
            secondMilliseconds = SystemClock.elapsedRealtime() - secondStartTime;
            secondUpdateTime = secondMilliseconds + secondCurrentTime;

            int secondSecs = (int) secondUpdateTime / 1000;
            int secondMinutes = secondSecs / 60;
            secondSecs = secondSecs % 60;
            int millis = (int) secondUpdateTime % 1000;

            String secondTime = String.format(Locale.getDefault(), "%02d:%02d:%03d", secondMinutes, secondSecs, millis);

            intervalTextView.setText(secondTime);

            secondHandler.postDelayed(this, 0);
        }
    };

}