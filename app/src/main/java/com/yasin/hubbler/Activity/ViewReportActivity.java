package com.yasin.hubbler.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yasin.hubbler.DatabaseClient;
import com.yasin.hubbler.Fragment.EditReportFragment;
import com.yasin.hubbler.Fragment.ViewReportFragment;
import com.yasin.hubbler.Hubbler;
import com.yasin.hubbler.Model.Report;
import com.yasin.hubbler.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by im_yasinashraf started on 16/11/18.
 */
public class ViewReportActivity extends AppCompatActivity implements View.OnClickListener {

    private int id;
    private String report;
    private ArrayList<String> fields;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView firstLetter;
    private FloatingActionButton editButton;
    private FloatingActionButton deleteButton;
    private AppBarLayout appBarLayout;
    private Boolean isInComposite = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);
        id = getIntent().getExtras().getInt("id");
        report = getIntent().getExtras().getString("json");
        fields = getIntent().getExtras().getStringArrayList("fields");
        init();
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        firstLetter = findViewById(R.id.tv_property_first_letter);
        editButton = findViewById(R.id.fab_edit);
        deleteButton = findViewById(R.id.fab_delete);
        appBarLayout = findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        showFields();
        editButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
    }

    public void setReport(String report) {
        this.report = report;
        createHeaders();
    }

    public String getReport() {
        return report;
    }

    /**
     * Add ViewReport fragment to container to show the fields.
     */
    private void showFields(){
        createHeaders();
        Bundle args = new Bundle();
        args.putStringArrayList("fields",fields);
        ViewReportFragment reportFragment = new ViewReportFragment();
        reportFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fade_in,R.animator.fade_out)
                .replace(R.id.container, reportFragment,"reportFragment")
                .commit();
    }

    /**
     * Add EditReport fragment to container to edit the fields.
     */
    private void showEditFields(){
        Bundle args = new Bundle();
        args.putInt("id",id);
        EditReportFragment editReportFragment = new EditReportFragment();
        editReportFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fade_in,R.animator.fade_out)
                .replace(R.id.container, editReportFragment,"editReportFragment")
                .addToBackStack("editReportFragment")
                .commit();
    }

    private void createHeaders(){
        JSONObject reportObject;
        try {
            reportObject = new JSONObject(report);
            collapsingToolbarLayout.setTitle(reportObject.get(fields.get(0)).toString());
            firstLetter.setText(String.valueOf(reportObject.get(fields.get(0)).toString().charAt(0)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void deleteReport(){
        Hubbler.getApp(this).getExecutor().execute(() -> {
            Report report = new Report();
            report.setReport(getReport());
            report.setId(id);
            DatabaseClient.getInstance(ViewReportActivity.this).getAppDatabase().reportDao().delete(report);
            finish();
            runOnUiThread(() -> Toast.makeText(ViewReportActivity.this, R.string.toast_report_deleted, Toast.LENGTH_SHORT).show());
        });
    }

    private void showDeleteDialog(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(R.string.label_are_you_sure);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                R.string.label_yes_delete,
                (dialog, id) -> {
                    deleteReport();
                });

        builder1.setNegativeButton(
                R.string.label_no_dont,
                (dialog, id) -> dialog.cancel());

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab_edit:
                showEditFields();
                editButton.hide();
                appBarLayout.setExpanded(false,true);
                break;

            case R.id.fab_delete:
                showDeleteDialog();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStack();
            editButton.show();
            appBarLayout.setExpanded(true,true);
        }else {
            super.onBackPressed();
        }
    }
}
