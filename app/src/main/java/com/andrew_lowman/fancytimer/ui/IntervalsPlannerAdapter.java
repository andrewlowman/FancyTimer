package com.andrew_lowman.fancytimer.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andrew_lowman.fancytimer.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class IntervalsPlannerAdapter extends RecyclerView.Adapter<IntervalsPlannerAdapter.IntervalsPlannerViewHolder> {

    private final LayoutInflater intervalsInflater;
    private final Context intervalsContext;
    private List<String> times;
    private int recentlyDeletedPosition;
    private String recentlyDeleted;

    public IntervalsPlannerAdapter(Context context) {
        this.intervalsInflater = LayoutInflater.from(context);
        this.intervalsContext = context;
    }

    public class IntervalsPlannerViewHolder extends RecyclerView.ViewHolder{
        private final TextView timeText;

        public IntervalsPlannerViewHolder(@NonNull View itemView) {
            super(itemView);
            timeText = itemView.findViewById(R.id.intervalListItemTextView);
        }
    }

    @NonNull
    @Override
    public IntervalsPlannerAdapter.IntervalsPlannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = intervalsInflater.inflate(R.layout.interval_list_item,parent,false);
        return new IntervalsPlannerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull IntervalsPlannerAdapter.IntervalsPlannerViewHolder holder, int position) {
        if(times != null){
            String current = times.get(position);
            holder.timeText.setText(current);
        }else{
            holder.timeText.setText("None");
        }
    }

    @Override
    public int getItemCount() {
        if(times != null){
            return times.size();
        }else{
            return 0;
        }
    }

    public void setTimes(List<String> newTimes){
        times = newTimes;
        notifyDataSetChanged();
    }

    public void deleteItem(int position){
        recentlyDeleted = times.get(position);
        recentlyDeletedPosition = position;
        times.remove(position);
        notifyItemRemoved(position);
        showUndoSnackBar();
    }

    private void showUndoSnackBar(){
        Activity activity = (Activity) intervalsContext;
        View view = activity.findViewById(R.id.intervalsPlannerConstraintLayout);
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
        times.add(recentlyDeletedPosition,recentlyDeleted);
        notifyItemInserted(recentlyDeletedPosition);
    }
}
