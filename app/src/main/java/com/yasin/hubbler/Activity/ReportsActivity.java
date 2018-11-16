package com.yasin.hubbler.Activity;

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

import com.yasin.hubbler.Adapter.ReportsAdapter;
import com.yasin.hubbler.DatabaseClient;
import com.yasin.hubbler.Hubbler;
import com.yasin.hubbler.Model.Report;
import com.yasin.hubbler.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReportsActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView rvReports;
    private FrameLayout addReportButton;
    private RelativeLayout emptyReports;
    private TextView reportsCount;
    private List<Report> reports;
    private ReportsAdapter reportsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        init();
    }

    private void init(){
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
        Hubbler.getApp(this).getExecutor().execute(()->{  // get report data from DB, different thread execution
            reports = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().reportDao().load();
            if(reports.size() == 0){
                this.runOnUiThread(() ->{ //switch back to UI thread for switching view
                    emptyReports.setVisibility(View.VISIBLE);
                });
            }else {
                this.runOnUiThread(() -> {//switch back to UI thread for switching view
                    reportsAdapter = new ReportsAdapter(reports,readJsonFileToGetFields(),this);
                    rvReports.setAdapter(reportsAdapter);
                    emptyReports.setVisibility(View.GONE);
                    reportsCount.setText(String.valueOf(reports.size())); // set count of NUmber of Reports.
                });
            }
        });
    }

    /**
     * read Fields from JSON file (Assets/file.json), Stored as ArrayList to preserve the order.
     */
    private List<String> readJsonFileToGetFields() {
        List<String> fields = new ArrayList<>();
        try {
            InputStream inputStream = this.getAssets().open("file.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String jsonData = new String(buffer, "UTF-8");

            JSONArray viewArray;
            try {
                viewArray = new JSONArray(jsonData);
                for (int i = 0; i < viewArray.length(); i++) { // for-each not applicable to jsonArray
                    JSONObject viewObject = viewArray.getJSONObject(i);
                    final String fieldName = viewObject.getString("field-name");
                    fields.add(fieldName);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return fields;
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
