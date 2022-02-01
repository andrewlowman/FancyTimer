package com.andrew_lowman.fancytimer.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.ColumnInfo;

import com.andrew_lowman.fancytimer.Activity.Intervals;
import com.andrew_lowman.fancytimer.R;

import java.util.List;

public class IntervalsAdapter extends RecyclerView.Adapter<IntervalsAdapter.IntervalsViewHolder> {

    private final LayoutInflater layoutInflater;
    private final Context context;
    private List<String> times;

    private int selectedPos = RecyclerView.NO_POSITION;
    private int prevPos = RecyclerView.NO_POSITION;

    public IntervalsAdapter(Context intervalsContext){
        this.layoutInflater = LayoutInflater.from(intervalsContext);
        this.context = intervalsContext;
    }

    public class IntervalsViewHolder extends RecyclerView.ViewHolder{
        private final TextView timeText;

        public IntervalsViewHolder(@NonNull View itemView) {
            super(itemView);
            timeText = itemView.findViewById(R.id.intervalListItemTextView);

        }
    }

    @NonNull
    @Override
    public IntervalsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.interval_list_item,parent,false);
        return new IntervalsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull IntervalsViewHolder holder, int position) {
        if(times != null){
            String current = times.get(position);
            holder.timeText.setText(current);
            if(position == selectedPos){
                holder.itemView.setBackgroundColor(Color.GREEN);
            }else{
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }
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

    /*@Override
    public void highlightEntry(int entry) {
        selectedPos = entry;
    }*/

    public void setBackground(int entryNumber){
        selectedPos = entryNumber;
        notifyItemChanged(prevPos);
        notifyItemChanged(selectedPos);
        prevPos = selectedPos;
    }
}
