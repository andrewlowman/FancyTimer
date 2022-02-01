package com.andrew_lowman.fancytimer.ViewModel;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.andrew_lowman.fancytimer.ui.IntervalsPlannerAdapter;
import com.andrew_lowman.fancytimer.ui.LoadIntervalAdapter;

public class IntervalsPlannerSwipeToDelete extends ItemTouchHelper.SimpleCallback{

    private IntervalsPlannerAdapter mAdapter;

    public IntervalsPlannerSwipeToDelete(IntervalsPlannerAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        mAdapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        mAdapter.deleteItem(position);
    }
}
