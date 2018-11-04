package com.yasin.hubbler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

public class ReportsActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView rvReports;
    private FrameLayout addReportButton;
    private RelativeLayout emptyReports;
    private TextView reportsCount;
    private List<Report> reports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        rvReports = findViewById(R.id.rv_reports);
        addReportButton = findViewById(R.id.button_add_report);
        emptyReports = findViewById(R.id.layout_empty_view);
        reportsCount = findViewById(R.id.tv_no_of_reports);

        rvReports.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.divider)));
        rvReports.addItemDecoration(itemDecorator);
        addReportButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setReports();
    }

    private void setReports(){
        HubblerDatabase hubblerDatabase = HubblerDatabase.getInMemoryDatabase(Hubbler.getApp(this));
        Hubbler.getApp(this).getExecutor().execute(()->{
            reports = hubblerDatabase.reportDao().load();
            if(reports.size() == 0){
                emptyReports.setVisibility(View.VISIBLE);
            }else {
                this.runOnUiThread(() -> {
                    ReportsAdapter reportsAdapter = new ReportsAdapter(reports);
                    rvReports.setAdapter(reportsAdapter);
                    emptyReports.setVisibility(View.GONE);
                    reportsCount.setText(String.valueOf(reports.size()));
                });

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_add_report:
                startActivity(new Intent(this,AddReportActivity.class));
                overridePendingTransition(R.anim.enter, R.anim.exit);
                break;
        }
    }
}
