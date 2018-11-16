package com.yasin.hubbler.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yasin.hubbler.Model.Report;
import com.yasin.hubbler.R;
import com.yasin.hubbler.ViewReportActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by im_yasinashraf started on 30/10/18.
 */
public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ReportsViewHolder> {

    private List<Report> reports;
    private List<String> fields;
    private Context context;

    public ReportsAdapter(List<Report> reports, List<String> fields,Context context) {
        this.reports = reports;
        this.fields = fields;
        this.context = context;
    }

    @NonNull
    @Override
    public ReportsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.report_list_item,viewGroup,false);
        return new ReportsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportsViewHolder reportsViewHolder, int i) {
        Report report = reports.get(i);
        try {
            JSONObject reportObject = new JSONObject(report.getReport());
            reportsViewHolder.identifierOne.setText(String.format("%s : %s",fields.get(0),reportObject.get(fields.get(0))));
            if(fields.size() > 1){
                reportsViewHolder.identifierTwo.setText(String.format("%s : %s",fields.get(1),reportObject.get(fields.get(1))));
            }
            reportsViewHolder.addedTime.setText(String.format("%s min ago",getTimeInterval(report.getAddedTime())));
            reportsViewHolder.firstLetter.setText(String.valueOf(reportObject.get(fields.get(0)).toString().charAt(0)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        reportsViewHolder.itemView.setOnClickListener(view ->{
            Intent intent = new Intent(context,ViewReportActivity.class);
            intent.putExtra("id",report.getId());
            intent.putExtra("json",report.getReport());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    private Long getTimeInterval(Date addedDate){
        Date currentDate = new Date();
        long duration  = currentDate.getTime() - addedDate.getTime();
        return TimeUnit.MILLISECONDS.toMinutes(duration);
    }

    class ReportsViewHolder extends RecyclerView.ViewHolder {

        private TextView firstLetter;
        private TextView identifierOne;
        private TextView identifierTwo;
        private TextView addedTime;

        ReportsViewHolder(@NonNull View itemView) {
            super(itemView);
            firstLetter = itemView.findViewById(R.id.tv_property_first_letter);
            identifierOne = itemView.findViewById(R.id.tv_property_identifier_1);
            identifierTwo = itemView.findViewById(R.id.tv_property_identifier_2);
            addedTime = itemView.findViewById(R.id.tv_added_time);
        }
    }
}
