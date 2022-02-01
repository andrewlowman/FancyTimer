package com.andrew_lowman.fancytimer.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andrew_lowman.fancytimer.Activity.Intervals;
import com.andrew_lowman.fancytimer.Activity.LoadInterval;
import com.andrew_lowman.fancytimer.Entities.IntervalsEntity;
import com.andrew_lowman.fancytimer.R;
import com.andrew_lowman.fancytimer.ViewModel.IntervalsViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class LoadIntervalAdapter extends RecyclerView.Adapter<LoadIntervalAdapter.LoadIntervalViewHolder> {

    private final LayoutInflater layoutInflater;
    private final Context context;
    private List<IntervalsEntity> intervals;
    private LoadIntervalAdapter.LoadIntervalAdapterListener mlistener;
    private IntervalsEntity recentlyDeleted;
    private int recentlyDeletedPosition;
    private IntervalsViewModel intervalsViewModel;
    private IntervalsEntity toBeDeleted;

    public LoadIntervalAdapter(Context intervalContext, LoadIntervalAdapter.LoadIntervalAdapterListener listener, IntervalsViewModel viewModel){
        this.layoutInflater = LayoutInflater.from(intervalContext);
        this.context = intervalContext;
        this.mlistener = listener;
        this.intervalsViewModel = viewModel;
    }

    public class LoadIntervalViewHolder extends RecyclerView.ViewHolder{
        private final TextView titleText;

        public LoadIntervalViewHolder(@NonNull View itemView, final LoadIntervalAdapter.LoadIntervalAdapterListener listener) {
            super(itemView);
            titleText = itemView.findViewById(R.id.loadIntervalListItemTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    final IntervalsEntity ie = intervals.get(position);
                    listener.userItemClicked(ie);
                }
            });
        }
    }

    @NonNull
    @Override
    public LoadIntervalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.load_interval_list_item,parent,false);
        return new LoadIntervalViewHolder(itemView,mlistener);
    }

    @Override
    public void onBindViewHolder(@NonNull LoadIntervalViewHolder holder, int position) {
        if(intervals != null){
            IntervalsEntity current = intervals.get(position);
            holder.titleText.setText(current.getName());
        }else{
            holder.titleText.setText("None");
        }
    }

    @Override
    public int getItemCount() {
        if(intervals != null){
            return intervals.size();
        }else{
            return 0;
        }
    }

    public void setIntervalTitles(List<IntervalsEntity> newIntervals){
        intervals = newIntervals;
        notifyDataSetChanged();
    }

    public interface LoadIntervalAdapterListener{
        void userItemClicked(IntervalsEntity ie);
    }

    public void deleteItem(int position){
        toBeDeleted = intervals.get(position);
        recentlyDeleted = intervals.get(position);
        recentlyDeletedPosition = position;
        intervals.remove(position);
        notifyItemRemoved(position);
        intervalsViewModel.delete(toBeDeleted.getIntervalID());
        showUndoSnackBar();
    }

    private void showUndoSnackBar(){
        Activity activity = (Activity) context;
        View view = activity.findViewById(R.id.loadTimerRecyclerView);
        Snackbar snackbar = Snackbar.make(view,"Delete?",Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoDelete();
            }
        });
        snackbar.show();
    }

    private void undoDelete(){
        intervals.add(recentlyDeletedPosition,recentlyDeleted);
        notifyItemInserted(recentlyDeletedPosition);
        intervalsViewModel.insert(toBeDeleted);
    }
}
