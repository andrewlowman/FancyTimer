package com.andrew_lowman.fancytimer.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
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

import com.andrew_lowman.fancytimer.Model.Clock;
import com.andrew_lowman.fancytimer.R;
import com.andrew_lowman.fancytimer.ui.IntervalsAdapter;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.internal.NavigationMenu;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Intervals extends AppCompatActivity implements Clock, Animation.AnimationListener{

    private long milliseconds;
    private long originalTime;
    private boolean running = false;
    private boolean started = false;
    private boolean loaded = false;

    private Button startButton;
    private Button loadButton;
    private Button resetButton;
    private Button newButton;
    private Button ringtoneButton;

    private TextView countdownTextView;

    private List<Long> longTimes = new ArrayList<>();
    private List<String> stringTimes = new ArrayList<>();
    private List<Character> instructions = new ArrayList<>();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intervals);

        countdownTextView = findViewById(R.id.countdownTextView);

        startButton = findViewById(R.id.intervalStartButton);
        loadButton = findViewById(R.id.intervalLoadButton);
        resetButton = findViewById(R.id.intervalResetButton);
        newButton = findViewById(R.id.intervalNewButton);
        ringtoneButton = findViewById(R.id.intervalRingtoneButton);

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
        //mListener = intervalsAdapter;

        ActivityResultLauncher<Intent> newIntervalActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent intent = result.getData();

                            stringTimes = (List<String>) intent.getSerializableExtra("StringTimesArray");
                            longTimes = (List<Long>) intent.getSerializableExtra("LongTimesArray");
                            originalTime = longTimes.get(n);
                            loaded = true;

                            loadTimers();

                            //copy timers for reset
                            backupTimers.addAll(timers);

                            //have to put these in here to load recycler
                            intervalsAdapter.setTimes(stringTimes);
                            countdownTextView.setText(convert(originalTime));


                        }
                    }
                });

        ActivityResultLauncher<Intent> loadIntervalActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent intent = result.getData();
                            longTimes = getLongFromString(intent.getStringExtra("code"));
                            stringTimes = convertLongListToString(longTimes);
                            originalTime = longTimes.get(n);
                            loaded = true;

                            loadTimers();

                            backupTimers.addAll(timers);

                            //have to put these in here to load recycler
                            intervalsAdapter.setTimes(stringTimes);
                            countdownTextView.setText(convert(originalTime));


                        }
                    }
                }
        );

        ActivityResultLauncher<Intent> loadRingtoneActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent intent = result.getData();
                            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                            mp = MediaPlayer.create(getApplicationContext(),uri);

                            loaded = true;
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
                                        if(n<timers.size()){
                                            ct = timers.get(n);
                                            ct.start();
                                        }else{
                                            loaded = false;
                                        }
                                        countdownTextView.setText("00:00:000");
                                        countdownTextView.startAnimation(blinkingAnimation);
                                        intervalsAdapter.setBackground(n);
                                        mp.start();
                                        n++;

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
                if(loaded) {
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

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loaded) {
                    if(running){
                        pause();
                        startButton.setText("Pause");
                    }
                    if (startButton.getText().toString().equals("Start")) {
                        if(stopwatch = true){
                            runTheLoop();
                            stopwatch = false;
                        }else{
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
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
                loaded = true;
                timers.clear();
                timers.addAll(backupTimers);
            }
        });

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
                Intent loadTimer = new Intent(Intervals.this,LoadInterval.class);
                loadIntervalActivityLauncher.launch(loadTimer);
                //startActivity(loadTimer);
            }
        });

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
                Intent intent = new Intent(Intervals.this,IntervalsPlanner.class);
                newIntervalActivityLauncher.launch(intent);
            }
        });

        ringtoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
                Intent loadRingtone = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                loadRingtone.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,"Select alert tone");
                loadRingtone.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                loadRingtone.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
                loadRingtone.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                loadRingtoneActivityLauncher.launch(loadRingtone);
            }
        });

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
                intervalsAdapter.setBackground(n);
                mp.start();
                runTheLoop();
                timersCounter++;
            }
        });
        ct = timers.get(timersCounter);
        ct.start();
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
            strings.add(convertToMinutesSeconds(l));
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
                        intervalsAdapter.setBackground(longTimesCounter + 1);
                        mp.start();
                        runTheLoop();
                        timersCounter++;
                    }
                });
            }
        }
    }

    public void stopwatchCancel(){
        mFirstHandler.removeCallbacks(mainRunnable);
        countdownTextView.setText("00:00:000");
        milliseconds = 0L;
        startTime = 0L;
        updateTime = 0L;
        currentTime = 0L;
        startButton.setText(convert(originalTime));
        running = false;
    }

    public void stopwatchStart(){
        startTime = SystemClock.elapsedRealtime();
        mFirstHandler.postDelayed(mainRunnable,0);
        currentTime += milliseconds;
        stopwatch = true;
        startButton.setText("Interval");
    }

    public void stopwatchPause(){
        mFirstHandler.removeCallbacks(mainRunnable);
    }

    public void runTheLoop(){
        if(longTimesCounter < longTimes.size()){
            if(longTimes.get(longTimesCounter)==0){
                stopwatchStart();
            }else{
                ct = timers.get(timersCounter);
                ct.start();
            }
            intervalsAdapter.setBackground(longTimesCounter);
            longTimesCounter++;
        }

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