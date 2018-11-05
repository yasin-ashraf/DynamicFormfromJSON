package com.yasin.hubbler;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by im_yasinashraf started on 30/10/18.
 */
public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ReportsViewHolder> {

    private List<Report> reports;

    ReportsAdapter(List<Report> reports) {
        this.reports = reports;
    }

    @NonNull
    @Override
    public ReportsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.report_list_item,viewGroup,false);
        return new ReportsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportsViewHolder reportsViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    class ReportsViewHolder extends RecyclerView.ViewHolder {

        ReportsViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
