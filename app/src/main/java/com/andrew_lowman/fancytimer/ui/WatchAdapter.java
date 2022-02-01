package com.andrew_lowman.fancytimer.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andrew_lowman.fancytimer.R;

import java.util.List;

public class WatchAdapter extends RecyclerView.Adapter<WatchAdapter.WatchViewHolder> {

    private final LayoutInflater watchInflater;
    private final Context watchContext;
    private List<String> times;

    public WatchAdapter(Context watchContext){
        watchInflater = LayoutInflater.from(watchContext);
        this.watchContext = watchContext;
    }

    public class WatchViewHolder extends RecyclerView.ViewHolder{
        private final TextView timeText;

        public WatchViewHolder(@NonNull View itemView) {
            super(itemView);
            timeText = itemView.findViewById(R.id.timeListItemTextView);
        }
    }

    @NonNull
    @Override
    public WatchAdapter.WatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = watchInflater.inflate(R.layout.timer_list_item,parent,false);
        return new WatchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchAdapter.WatchViewHolder holder, int position) {
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
}
