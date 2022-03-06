package com.andrew_lowman.fancytimer.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.andrew_lowman.fancytimer.Entities.IntervalsEntity;
import com.andrew_lowman.fancytimer.Entities.ReportEntity;
import com.andrew_lowman.fancytimer.Model.Clock;
import com.andrew_lowman.fancytimer.R;
import com.andrew_lowman.fancytimer.ViewModel.IntervalsViewModel;
import com.andrew_lowman.fancytimer.ViewModel.ReportsViewModel;
import com.andrew_lowman.fancytimer.ui.IntervalsAdapter;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.internal.NavigationMenu;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Intervals extends AppCompatActivity implements Clock, Animation.AnimationListener{

    private long milliseconds;
    private long originalTime;
    private boolean running = false;
    private boolean started = false;
    private boolean timerReady = false;

    private Button startButton;
    private Button loadButton;
    private Button resetButton;
    private Button newButton;
    private Button ringtoneButton;
    private Button reportButton;

    private TextView countdownTextView;
    private TextView nameTextView;

    private List<Long> longTimes = new ArrayList<>();
    private List<String> stringTimes = new ArrayList<>();
    private int n = 0;

    private List<CountDownTimer> timers = new ArrayList<>();
    private List<CountDownTimer> backupTimers = new ArrayList<>();
    private CountDownTimer ct;

    private IntervalsAdapter intervalsAdapter;

    private Uri notification;
    private MediaPlayer mp;

    private Animation blinkingAnimation;

    private long startTime;
    private long updateTime;
    private long currentTime;
    private final Handler mFirstHandler = new Handler();

    private int longTimesCounter = 0;
    private int timersCounter = 0;
    private boolean stopwatch = false;
    private boolean timerStarted = false;

    private int intervalID;
    private int reportID = 0;
    private String intervalName = "";

    private ReportsViewModel reportsViewModel;
    private List<ReportEntity> reports = new ArrayList<>();
    private List<IntervalsEntity> intervals = new ArrayList<>();

    private ActivityResultLauncher<Intent> loadRingtoneActivityLauncher;
    private ActivityResultLauncher<Intent> loadIntervalActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intervals);

        countdownTextView = findViewById(R.id.countdownTextView);
        nameTextView = findViewById(R.id.intervalsNameTextView);
        nameTextView.setVisibility(View.INVISIBLE);

        startButton = findViewById(R.id.intervalStartButton);
        //loadButton = findViewById(R.id.intervalLoadButton);
        resetButton = findViewById(R.id.intervalResetButton);
        newButton = findViewById(R.id.intervalNewButton);
        //ringtoneButton = findViewById(R.id.intervalRingtoneButton);
        //reportButton = findViewById(R.id.intervalRunReportButton);

        reportsViewModel = new ViewModelProvider(this).get(ReportsViewModel.class);
        reportsViewModel.getAllReports().observe(this, new Observer<List<ReportEntity>>() {
            @Override
            public void onChanged(List<ReportEntity> reportEntities) {
                reports.addAll(reportEntities);
            }
        });

        IntervalsViewModel intervalsViewModel = new ViewModelProvider(this).get(IntervalsViewModel.class);
        intervalsViewModel.getAllIntervals().observe(this, new Observer<List<IntervalsEntity>>() {
            @Override
            public void onChanged(List<IntervalsEntity> intervalsEntityList) {
                intervals.addAll(intervalsEntityList);
            }
        });
        NavigationBarView navigationView = findViewById(R.id.bottomNavigation);
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch(item.getItemId()){
                    case R.id.stopwatch_menu_item:
                        intent =  new Intent(Intervals.this,Watch.class);
                        startActivity(intent);
                        return true;
                    case R.id.timer_menu_item:
                        intent = new Intent(Intervals.this,Timer.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });

        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mp = MediaPlayer.create(getApplicationContext(),notification);

        blinkingAnimation = new AlphaAnimation(0.0f,1.0f);
        blinkingAnimation.setDuration(500);
        blinkingAnimation.setStartOffset(20);
        blinkingAnimation.setRepeatMode(Animation.REVERSE);
        blinkingAnimation.setRepeatCount(10);

        //bottomAppBar = findViewById(R.id.bar);

        RecyclerView recyclerView = findViewById(R.id.intervalsRecyclerView);
        intervalsAdapter = new IntervalsAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(intervalsAdapter);

        ActivityResultLauncher<Intent> newIntervalActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent intent = result.getData();

                            stringTimes = (List<String>) intent.getSerializableExtra("StringTimesArray");
                            longTimes = (List<Long>) intent.getSerializableExtra("LongTimesArray");
                            intervalID = intent.getIntExtra("intervalID", 0);
                            intervalName = intent.getStringExtra("name");
                            //System.out.println("IntervalID: " + intervalID);
                            originalTime = longTimes.get(n);
                            timerReady = true;

                            loadTimers();

                            //copy timers for reset
                            backupTimers.clear();
                            backupTimers.addAll(timers);

                            //have to put these in here to load recycler
                            intervalsAdapter.setTimes(stringTimes);
                            countdownTextView.setText(convert(originalTime));
                            nameTextView.setText(intervalName);
                            nameTextView.setPaintFlags(nameTextView.getPaintFlags() |  Paint.UNDERLINE_TEXT_FLAG);
                            nameTextView.setVisibility(View.VISIBLE);
                        }
                    }
                });

        loadIntervalActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent intent = result.getData();
                            longTimes = getLongFromString(intent.getStringExtra("code"));
                            stringTimes = convertLongListToString(longTimes);
                            intervalID = intent.getIntExtra("intervalID", 0);
                            intervalName = intent.getStringExtra("name");
                            originalTime = longTimes.get(n);
                            timerReady = true;

                            loadTimers();

                            //need a second array since restarting a timer alters it
                            backupTimers.clear();
                            backupTimers.addAll(timers);

                            //have to put these in here to load recycler
                            intervalsAdapter.setTimes(stringTimes);
                            countdownTextView.setText(convert(originalTime));
                            nameTextView.setText(intervalName);
                            nameTextView.setPaintFlags(nameTextView.getPaintFlags() |  Paint.UNDERLINE_TEXT_FLAG);
                            nameTextView.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );

        loadRingtoneActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent intent = result.getData();
                            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                            mp = MediaPlayer.create(getApplicationContext(),uri);

                            timerReady = true;
                            timers.clear();

                            for(int i=0; i<longTimes.size(); i++){
                                timers.add(new CountDownTimer(longTimes.get(i),1) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        milliseconds = millisUntilFinished;
                                        countdownTextView.setText(convert(millisUntilFinished));
                                    }

                                    @Override
                                    public void onFinish() {
                                        countdownTextView.setText("00:00:000");
                                        countdownTextView.startAnimation(blinkingAnimation);
                                        mp.start();
                                        runTheLoop();
                                    }
                                });
                            }
                        }
                    }
                }
        );

        /*startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timerReady) {
                    if (startButton.getText().toString().equals("Start")) {
                        if (running) {
                            start(milliseconds);
                            //System.out.println("Restart");
                        } else {
                            start();
                            running = true;
                        }
                        if (originalTime > 0) {
                            startButton.setText("Pause");
                        }
                    } else if (startButton.getText().toString().equals("Pause")) {
                        pause();
                        startButton.setText("Start");
                    }
                }
            }
        });*/

        /*startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timerReady) {
                    if (running) {
                        //if running and is a stopwatch - stop stopwatch and next in loop
                        if(stopwatch){
                            stopwatchCancel();
                            runTheLoop();
                            //if running and not a stopwatch - pause/set text to start
                        }else{
                            pause();
                            startButton.setText("Start");
                        }
                        //if not running but has been started and is not stopwatch -  restart
                    } else if(started){
                        if(!stopwatch){
                            start(milliseconds);
                        }
                        //if not started - run loop started is true
                    }else{
                        runTheLoop();
                        started = true;
                    }
                    if (originalTime > 0) {
                        startButton.setText("Pause");
                    }
                }
            }
        });*/

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timerReady) {
                    //if not started
                    if(!started){
                        runTheLoop();
                    }else{
                        //if started
                        if(stopwatch){
                            //stopwatch starts auto doesnt need a button only stops and isnt pausable
                            stopwatchCancel();
                            runTheLoop();
                        }else{
                            //if timer is running pause the timer
                            if(running){
                                pause();
                                running = false;
                                startButton.setText("Start");
                                //if timer not running restart
                            }else {
                                start(milliseconds);
                                running = true;
                                startButton.setText("Pause");
                            }
                        }
                    }
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetEverything();
                intervalsAdapter.resetTimes();
            }
        });

        /*loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetEverything();
                intervalsAdapter.resetTimes();
                Intent loadTimer = new Intent(Intervals.this,LoadInterval.class);
                loadIntervalActivityLauncher.launch(loadTimer);
            }
        });*/

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetEverything();
                intervalsAdapter.resetTimes();
                Intent intent = new Intent(Intervals.this,IntervalsPlanner.class);
                newIntervalActivityLauncher.launch(intent);
            }
        });

        /*ringtoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetEverything();
                intervalsAdapter.resetTimes();
                Intent loadRingtone = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                loadRingtone.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,"Select alert tone");
                loadRingtone.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                loadRingtone.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
                loadRingtone.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                loadRingtoneActivityLauncher.launch(loadRingtone);
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intervals.this,Report.class);
                intent.putExtra("intervalID",intervalID);
                startActivity(intent);
            }
        });*/

        /*bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if(id==R.id.watch_image_button_menu_item){
                    Intent intent =  new Intent(Intervals.this,Watch.class);
                    startActivity(intent);

                    return true;
                }

                if(id==R.id.timer_image_button_menu_item){
                    Intent intent = new Intent(Intervals.this,Timer.class);
                    startActivity(intent);

                    return true;
                }

                return false;
            }
        });*/

    }

    @Override
    public void start() {
       runTheLoop();
    }

    @Override
    public void start(long timer) {
        //replace current timer running so it can pick up where it stopped w/o restarting
        //countdown one to get to current timer running
        timersCounter--;
        timers.remove(timersCounter);
        timers.add(timersCounter,new CountDownTimer(timer,1) {
            @Override
            public void onTick(long millisUntilFinished) {
                milliseconds = millisUntilFinished;
                countdownTextView.setText(convert(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                countdownTextView.setText("00:00:000");
                countdownTextView.startAnimation(blinkingAnimation);
                mp.start();
                runTheLoop();
            }
        });
        ct = timers.get(timersCounter);
        ct.start();
        //add one to get to where it was
        timersCounter++;
    }

    @Override
    public void pause() {
        ct.cancel();
    }

    @Override
    public void cancel() {
        if(running){
            ct.cancel();
            n = 0;
            running = false;
            countdownTextView.setText(convert(originalTime));
            startButton.setText("Start");
        }
    }

    public String convert(long time){
        int hours = (int) ((time / (1000*60*60)) % 24);
        int minutes = (int) ((time / (1000 * 60)) % 60);
        int secs = (int) (time / 1000) % 60;
        int mils = (int) time % 1000;
        /*int secs = (int) time / 1000;
        int minutes = secs / 60;
        secs = secs % 60;
        int mils = (int) time % 1000;*/

        if(hours==0){
            return String.format(Locale.getDefault(), "%02d:%02d:%03d", minutes, secs, mils);
        }else{
            return String.format(Locale.getDefault(), "%02d:%02d:%02d:%03d", hours, minutes, secs, mils);
        }
    }

    public String convertToMinutesSeconds(long time){
        int minutes = (int) ((time / (1000 * 60)) % 60);
        int secs = (int) (time / 1000) % 60;

        return String.format(Locale.getDefault(),"%02d:%02d",minutes,secs);
    }

    public List<String> convertLongListToString(List<Long> longList){
        List<String> strings = new ArrayList<>();
        for(long l:longList){
            if(l==0L){
                strings.add("Stopwatch");
            }else{
                strings.add(convertToMinutesSeconds(l));
            }
        }

        return strings;
    }

    //public void next(long time) {
        //start(time);
    //}

    public List<Long> getLongFromString(String code){
        String[] stringLong = code.split(",");

        List<Long> longs = new ArrayList<>();

        for(int i = 0;i<stringLong.length;i++){
            longs.add(Long.valueOf(stringLong[i]));
        }

        return longs;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public void loadTimers(){
        timers.clear();

        for(int i=0; i<longTimes.size(); i++){
            if(longTimes.get(i)!=0){
                timers.add(new CountDownTimer(longTimes.get(i),1) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        milliseconds = millisUntilFinished;
                        countdownTextView.setText(convert(millisUntilFinished));
                    }

                    @Override
                    public void onFinish() {
                        countdownTextView.setText("00:00:000");
                        countdownTextView.startAnimation(blinkingAnimation);
                        mp.start();
                        runTheLoop();

                    }
                });
            }
        }
    }

    public void stopwatchCancel(){
        mFirstHandler.removeCallbacks(mainRunnable);
        intervalsAdapter.updateStopWatchEntry(longTimesCounter - 1,countdownTextView.getText().toString());
        countdownTextView.setText("00:00:000");
        milliseconds = 0L;
        startTime = 0L;
        updateTime = 0L;
        currentTime = 0L;
        startButton.setText("Start");
        running = false;
        stopwatch = false;
        if(longTimesCounter >= longTimes.size()){
            timerReady = false;
        }
    }

    public void stopwatchStart(){
        startTime = SystemClock.elapsedRealtime();
        mFirstHandler.postDelayed(mainRunnable,0);
        currentTime += milliseconds;
        stopwatch = true;
        startButton.setText("Interval");
        running = true;
    }

    public void stopwatchPause(){
        mFirstHandler.removeCallbacks(mainRunnable);
    }

    /*public void runTheLoop(){
        if(longTimesCounter < longTimes.size()){
            if(longTimes.get(longTimesCounter)==0){
                stopwatchStart();
                startButton.setText("Next");
            }else{
                if(timersCounter < timers.size()){
                    stopwatch = false;
                    ct = timers.get(timersCounter);
                    ct.start();
                    running = true;
                    startButton.setText("Pause");
                }
            }
            intervalsAdapter.setBackground(longTimesCounter);
            longTimesCounter++;
        }
    }*/

    //trying to keep running through the arrays in one method
    public void runTheLoop(){
        if(longTimesCounter < longTimes.size()){
            if(longTimes.get(longTimesCounter)==0){
                stopwatchStart();
            }else{
                if(timersCounter < timers.size()){
                    ct = timers.get(timersCounter);
                    ct.start();
                    running = true;
                    startButton.setText("Pause");
                    timersCounter++;
                }
            }
            started = true;
            intervalsAdapter.setBackground(longTimesCounter);
            longTimesCounter++;
        }else{
            timerReady = false;
            adaptTimes(intervalsAdapter.retrieveTimes());
        }
    }

    public void resetEverything(){
        try{
            ct.cancel();
        }catch (Exception e){

        }
        mFirstHandler.removeCallbacks(mainRunnable);
        milliseconds = 0L;
        startTime = 0L;
        updateTime = 0L;
        currentTime = 0L;
        stopwatch = false;
        running = false;
        started = false;
        timerReady = true;
        timers.clear();
        timers.addAll(backupTimers);
        timersCounter = 0;
        longTimesCounter = 0;
        countdownTextView.setText(convert(originalTime));
        startButton.setText("Start");
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

            countdownTextView.setText(time);

            mFirstHandler.postDelayed(this, 0);
        }
    };

    public void adaptTimes(List<String> stringTimes){
        //count to check if there is a report
        int count = 0;
        //string for interval name
        String name = "";
        //add date to start of string with an a to id it
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String newString = "";
        newString += "a" + date + ",";
        for(int i = 0;i<stringTimes.size();i++){
            newString += stringTimes.get(i) + ",";
        }

        for(IntervalsEntity ie:intervals){
            if(ie.getIntervalID()==intervalID){
                name = ie.getName();
            }
        }
        if(reports.size() > 0){
            for(ReportEntity re:reports){
                if(re.getFkIntervalID()==intervalID){
                    count++;
                    reportsViewModel.updateTimesRun(re.getReportID(),re.getNumberOfTimesRun() + 1);
                    String time = re.getReportCode() + newString;
                    reportsViewModel.updateReport(re.getReportID(),time);
                    //System.out.println("Count == 1");
                }
            }
        }
        if(count==0){
            reportsViewModel.insertReport(new ReportEntity(intervalID,name,newString,1));
            //System.out.println("Count == 0");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_intervals, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();

        if(id == R.id.reportMenuItem){
            Intent intent = new Intent(Intervals.this,Report.class);
            intent.putExtra("intervalID",intervalID);
            startActivity(intent);

            return true;
        }

        if(id == R.id.ringtoneMenuItem){
            resetEverything();
            intervalsAdapter.resetTimes();
            Intent loadRingtone = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            loadRingtone.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,"Select alert tone");
            loadRingtone.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
            loadRingtone.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
            loadRingtone.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
            loadRingtoneActivityLauncher.launch(loadRingtone);
            return true;
        }

        if(id == R.id.loadIntervalMenuItem){
            resetEverything();
            intervalsAdapter.resetTimes();
            Intent loadTimer = new Intent(Intervals.this,LoadInterval.class);
            loadIntervalActivityLauncher.launch(loadTimer);
        }

        return super.onOptionsItemSelected(item);
    }


    /*@Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.stopwatch_menu_item){
            Intent intent =  new Intent(Intervals.this,Watch.class);
            startActivity(intent);

            return true;
        }
        return false;
    }*/

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.bottom_app_bar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id==R.id.stopwatch_menu_item){
            Intent intent =  new Intent(Intervals.this,Watch.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

}