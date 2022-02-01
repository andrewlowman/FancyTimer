package com.andrew_lowman.fancytimer.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.andrew_lowman.fancytimer.Entities.IntervalsEntity;
import com.andrew_lowman.fancytimer.R;
import com.andrew_lowman.fancytimer.ViewModel.IntervalsViewModel;
import com.andrew_lowman.fancytimer.ViewModel.SwipeToDelete;
import com.andrew_lowman.fancytimer.ui.LoadIntervalAdapter;

import java.util.List;

public class LoadInterval extends AppCompatActivity implements LoadIntervalAdapter.LoadIntervalAdapterListener{

    private IntervalsViewModel intervalsViewModel;

    private List<IntervalsEntity> intervalTitles;

    private Button cancelButton;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_timer);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        cancelButton = findViewById(R.id.loadIntervalCancelButton);
        deleteButton = findViewById(R.id.loadIntervalDeleteButton);

        intervalsViewModel = new ViewModelProvider(this).get(IntervalsViewModel.class);

        RecyclerView recyclerView = findViewById(R.id.loadTimerRecyclerView);
        final LoadIntervalAdapter loadIntervalAdapter = new LoadIntervalAdapter(this,this,intervalsViewModel);
        recyclerView.setAdapter(loadIntervalAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDelete(loadIntervalAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        intervalsViewModel.getAllIntervals().observe(this, new Observer<List<IntervalsEntity>>() {
            @Override
            public void onChanged(List<IntervalsEntity> intervalsEntities) {
                loadIntervalAdapter.setIntervalTitles(intervalsEntities);
            }
        });

        loadIntervalAdapter.notifyDataSetChanged();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

    @Override
    public void userItemClicked(IntervalsEntity ie) {
        Intent intent = new Intent(LoadInterval.this,Intervals.class);
        intent.putExtra("name", ie.getName());
        intent.putExtra("code",ie.getCode());
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
}