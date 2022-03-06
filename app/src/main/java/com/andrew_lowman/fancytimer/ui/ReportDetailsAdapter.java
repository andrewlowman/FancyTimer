package com.andrew_lowman.fancytimer.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.andrew_lowman.fancytimer.R;

import java.util.List;

public class ReportDetailsAdapter extends RecyclerView.Adapter<ReportDetailsAdapter.ReportDetailsViewHolder> {
    private final LayoutInflater layoutInflater;
    private final Context context;
    private List<String> reportDetails;

    public ReportDetailsAdapter(Context reportsContext){
        this.layoutInflater = LayoutInflater.from(reportsContext);
        this.context = reportsContext;
    }

    public class ReportDetailsViewHolder extends RecyclerView.ViewHolder{
        private final TextView nameText;

        public ReportDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.reportListItemTextView);
        }
    }

    @NonNull
    @Override
    public ReportDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.reports_list_item,parent,false);
        return new ReportDetailsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportDetailsViewHolder holder, int position) {
        if(reportDetails != null){
            String current = reportDetails.get(position);
            if(current.startsWith("a")){
                holder.nameText.setText(current.substring(1));
                holder.itemView.setBackgroundColor(Color.LTGRAY);
            }else{
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                holder.nameText.setText("           " + current);
            }
        }else{
            holder.nameText.setText("None");
        }
    }



    @Override
    public int getItemCount() {
        if(reportDetails != null){
            return reportDetails.size();
        }else{
            return 0;
        }
    }


    public void setReports(List<String> newReports){
        reportDetails = newReports;
        notifyDataSetChanged();
    }
}