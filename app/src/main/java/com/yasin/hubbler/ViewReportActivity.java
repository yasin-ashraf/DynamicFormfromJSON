package com.yasin.hubbler;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by im_yasinashraf started on 16/11/18.
 */
public class ViewReportActivity extends AppCompatActivity {

    private int id;
    private String report;
    private ArrayList<String> fields;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView firstLetter;
    private LinearLayout container;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);
        id = getIntent().getExtras().getInt("id");
        report = getIntent().getExtras().getString("json");
        init();
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        container = findViewById(R.id.container);
        firstLetter = findViewById(R.id.tv_property_first_letter);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fields = new ArrayList<>();
        fields = readJsonFileToGetFields();
        parseJson();
        showFields();
    }
    /**
     * Add fragment to container to show the fields.
     */
    private void showFields(){
        Bundle args = new Bundle();
        args.putString("json",report);
        args.putStringArrayList("fields",fields);
        ViewReportFragment reportFragment = new ViewReportFragment();
        reportFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, reportFragment,"reportFragment")
                .commit();
    }

    private void parseJson(){
        JSONObject reportObject;
        try {
            reportObject = new JSONObject(report);
            collapsingToolbarLayout.setTitle(reportObject.get(fields.get(0)).toString());
            firstLetter.setText(String.valueOf(reportObject.get(fields.get(0)).toString().charAt(0)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> readJsonFileToGetFields() {
        ArrayList<String> fields = new ArrayList<>();
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
}
