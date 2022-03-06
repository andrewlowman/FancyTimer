package com.andrew_lowman.fancytimer.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.andrew_lowman.fancytimer.Activity.ReportDetails;
import com.andrew_lowman.fancytimer.Entities.ReportEntity;
import com.andrew_lowman.fancytimer.R;
import com.andrew_lowman.fancytimer.ViewModel.IntervalsViewModel;
import com.andrew_lowman.fancytimer.ViewModel.ReportsViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ReportsViewHolder> {
    private final LayoutInflater layoutInflater;
    private final Context context;
    private List<ReportEntity> reportNames;
    private int recentlyDeletedPosition;
    private ReportEntity recentlyDeleted;
    private ReportsViewModel reportsViewModel;
    private ReportEntity toBeDeleted;

    public ReportsAdapter(Context reportsContext,ReportsViewModel viewModel){
        this.layoutInflater = LayoutInflater.from(reportsContext);
        this.context = reportsContext;
        this.reportsViewModel = viewModel;
    }

    public class ReportsViewHolder extends RecyclerView.ViewHolder{
        private final TextView nameText;

        public ReportsViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.reportListItemTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    final ReportEntity re = reportNames.get(position);
                    Intent intent = new Intent(context, ReportDetails.class);
                    intent.putExtra("id",re.getReportID());
                    intent.putExtra("fkID",re.getFkIntervalID());
                    intent.putExtra("code",re.getReportCode());
                    intent.putExtra("timesRun",re.getNumberOfTimesRun());
                    intent.putExtra("name",re.getIntervalName());

                    context.startActivity(intent);
                }
            });
        }
    }

    @NonNull
    @Override
    public ReportsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.reports_list_item,parent,false);
        return new ReportsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportsViewHolder holder, int position) {
        if(reportNames != null){
            ReportEntity current = reportNames.get(position);
            holder.nameText.setText(Html.fromHtml("<b>" + "Interval ID: " + "</b>" + current.getFkIntervalID() + "<b>" + "        Name: " + "</b>" + current.getIntervalName()));
        }else{
            holder.nameText.setText("None");
        }
    }

    @Override
    public int getItemCount() {
        if(reportNames != null){
            return reportNames.size();
        }else{
            return 0;
        }
    }


    public void setReports(List<ReportEntity> newReports){
        reportNames = newReports;
        notifyDataSetChanged();
    }

    public void deleteItem(int position){
        toBeDeleted = reportNames.get(position);
        recentlyDeleted = reportNames.get(position);
        recentlyDeletedPosition = position;
        reportNames.remove(position);
        notifyItemRemoved(position);
        reportsViewModel.deleteReport(toBeDeleted.getReportID());
        showUndoSnackBar();
    }

    private void showUndoSnackBar(){
        Activity activity = (Activity) context;
        View view = activity.findViewById(R.id.reportRecyclerView);
        Snackbar snackbar = Snackbar.make(view,"Delete",Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoDelete();
            }
        });
        snackbar.show();
    }

    private void undoDelete(){
        reportNames.add(recentlyDeletedPosition,recentlyDeleted);
        notifyItemInserted(recentlyDeletedPosition);
        reportsViewModel.insertReport(toBeDeleted);
    }
}
