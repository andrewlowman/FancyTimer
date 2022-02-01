package com.andrew_lowman.fancytimer.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.andrew_lowman.fancytimer.R;

public class AddTimer extends AppCompatActivity {

    private NumberPicker npHours;
    private NumberPicker npMinutes;
    private NumberPicker npSeconds;

    private Button addButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_timer);

        npHours = findViewById(R.id.addTimerHoursNumberPicker);
        npMinutes = findViewById(R.id.addTimerMinutesNumberPicker);
        npSeconds = findViewById(R.id.addTimerSecondsNumberPicker);

        addButton = findViewById(R.id.addTimerAddButton);
        cancelButton = findViewById(R.id.addTimerCancelButton);

        npHours.setMaxValue(99);
        npHours.setMinValue(0);

        npMinutes.setMaxValue(59);
        npMinutes.setMinValue(0);

        npSeconds.setMaxValue(59);
        npSeconds.setMinValue(0);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long pickedHours = npHours.getValue() * 3600000L;
                long pickedMinutes = npMinutes.getValue() * 60000L;
                long pickedSeconds = npSeconds.getValue() * 1000L;
                long millis = pickedHours + pickedMinutes + pickedSeconds;
                Intent intent = new Intent(AddTimer.this,Timer.class);
                intent.putExtra("Milliseconds",millis);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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