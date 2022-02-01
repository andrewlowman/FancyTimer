package com.andrew_lowman.fancytimer.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.andrew_lowman.fancytimer.Model.Clock;
import com.andrew_lowman.fancytimer.R;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Locale;

public class Timer extends AppCompatActivity implements Clock {

    private TextView countdownTimerTextView;

    private CountDownTimer countdownTimer;
    private long milliseconds;
    private long originalTime = 0;

    private boolean started = false;

    private Button startButton;
    private Button addButton;
    private Button resetButton;

    private Animation blinkingAnimation;
    private Uri notification;
    private MediaPlayer mp;

    private CountDownTimer ct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        countdownTimerTextView = findViewById(R.id.countdownTimerTextView);

        startButton = findViewById(R.id.countdownTimerStartButton);
        addButton = findViewById(R.id.countdownTimerAddButton);
        resetButton = findViewById(R.id.countdownTimerResetButton);

        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mp = MediaPlayer.create(getApplicationContext(),notification);

        blinkingAnimation = new AlphaAnimation(0.0f,1.0f);
        blinkingAnimation.setDuration(500);
        blinkingAnimation.setStartOffset(20);
        blinkingAnimation.setRepeatMode(Animation.REVERSE);
        blinkingAnimation.setRepeatCount(10);

        NavigationBarView navigationView = findViewById(R.id.bottomNavigation);
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch(item.getItemId()){
                    case R.id.stopwatch_menu_item:
                        intent =  new Intent(Timer.this,Watch.class);
                        startActivity(intent);
                        return true;
                    case R.id.intervals_menu_item:
                        intent = new Intent(Timer.this,Intervals.class);
                        startActivity(intent);
                        return true;
                    case R.id.timer_menu_item:
                        return true;
                }
                return false;
            }
        });

        ActivityResultLauncher<Intent> addTimerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>(){
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            cancel();
                            Intent intent = result.getData();
                            originalTime = intent.getLongExtra("Milliseconds",0);
                            countdownTimerTextView.setText(convert(originalTime));
                        }
                    }
                });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startButton.getText().toString().equals("Start")){
                    if(started){
                        start(milliseconds);
                    }else{
                        start();
                        started = true;
                    }
                    if(originalTime > 0){
                        startButton.setText("Pause");
                    }
                }else if(startButton.getText().toString().equals("Pause")){
                    pause();
                    startButton.setText("Start");
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Timer.this,AddTimer.class);
                addTimerLauncher.launch(intent);
            }
        });
    }

    @Override
    public void start() {
        ct = new CountDownTimer(originalTime, 1) {

            @Override
            public void onTick(long millisUntilFinished) {
                milliseconds = millisUntilFinished;
                /*int secs = (int) millisUntilFinished / 1000;
                int minutes = secs / 60;
                secs = secs % 60;
                int mils = (int) millisUntilFinished % 1000;

                String time = String.format(Locale.getDefault(), "%02d:%02d:%03d", minutes, secs, mils);*/
                countdownTimerTextView.setText(convert(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                countdownTimerTextView.setText("00:00:000");
                countdownTimerTextView.startAnimation(blinkingAnimation);
                mp.start();
            }
        };

        ct.start();
    }

    @Override
    public void start(long timer) {
        ct = new CountDownTimer(timer, 1) {

            @Override
            public void onTick(long millisUntilFinished) {
                milliseconds = millisUntilFinished;
                /*int secs = (int) millisUntilFinished / 1000;
                int minutes = secs / 60;
                secs = secs % 60;
                int mils = (int) millisUntilFinished % 1000;

                String time = String.format(Locale.getDefault(), "%02d:%02d:%03d", minutes, secs, mils);*/
                countdownTimerTextView.setText(convert(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                countdownTimerTextView.setText("00:00:000");
            }
        };

        ct.start();
    }

    @Override
    public void pause() {

        ct.cancel();
    }

    @Override
    public void cancel() {
        if(started){
            started = false;
            ct.cancel();
            countdownTimerTextView.setText(convert(originalTime));
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
}