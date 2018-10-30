package com.yasin.hubbler;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.Objects;

public class ReportsActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView rvReports;
    private FrameLayout addReportButton;
    private RelativeLayout emptyReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        rvReports = findViewById(R.id.rv_reports);
        addReportButton = findViewById(R.id.button_add_report);
        emptyReports = findViewById(R.id.layout_empty_view);

        rvReports.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.divider)));
        rvReports.addItemDecoration(itemDecorator);
        addReportButton.setOnClickListener(this);

        setReports();
    }

    private void setReports(){
        ReportsAdapter reportsAdapter = new ReportsAdapter();
        rvReports.setAdapter(reportsAdapter);

    }

    @Override
    public void onClick(View view) {

    }
}
