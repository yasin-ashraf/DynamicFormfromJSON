package com.yasin.hubbler.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yasin.hubbler.DatabaseClient;
import com.yasin.hubbler.EventBus.OnReportUpdateEvent;
import com.yasin.hubbler.Fragment.AddReportFragment;
import com.yasin.hubbler.Hubbler;
import com.yasin.hubbler.Model.Report;
import com.yasin.hubbler.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Stack;

/**
 * Created by im_yasinashraf started on 1/11/18.
 */
public class AddReportActivity extends AppCompatActivity implements View.OnClickListener {

    private FrameLayout container;
    private ImageView backButton;
    private TextView pageTitle;
    private JSONObject reportObject;
    private Stack<String> titles;
    private Stack<String> bluePrints;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_add_report);
        reportObject = new JSONObject();
        titles = new Stack<>();
        bluePrints = new Stack<>();
        init();
        readJsonFile();
    }

    private void init(){
        container = findViewById(R.id.container);
        backButton = findViewById(R.id.iv_button_back);
        pageTitle = findViewById(R.id.label_add_a_report);

        backButton.setOnClickListener(this);
    }

    private void readJsonFile() {
        try {
            InputStream inputStream = this.getAssets().open("file.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String jsonData = new String(buffer, "UTF-8");

            showReportEditor(jsonData);

            Log.e(getString(R.string.label_data), jsonData);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void showReportEditor(String json){
        bluePrints.push(json);
        titles.push(getString(R.string.label_a_report));
        Bundle args = new Bundle();
        args.putString(getString(R.string.label_fields),json);
        args.putString(getString(R.string.label_report),reportObject.toString());
        AddReportFragment addReportFragment = new AddReportFragment();
        addReportFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fade_in,R.animator.fade_out)
                .replace(R.id.container, addReportFragment,"addReportFragment")
                .commit();
    }

    public void replaceWithCompositeFragment(String fieldName, String compositeFields, String value){
        bluePrints.push(compositeFields);
        pageTitle.setText(String.format(getString(R.string.label_add_a),fieldName));
        titles.push(fieldName);
        Bundle args = new Bundle();
        args.putString(getString(R.string.label_fields),compositeFields);
        args.putString(getString(R.string.label_fieldname),fieldName);
        args.putString(getString(R.string.label_report), value.equals("") ? reportObject.toString() : value);
        AddReportFragment compositeFragment = new AddReportFragment();
        compositeFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.enter,R.animator.exit,R.animator.enter_rev,R.animator.exit_rev)
                .replace(R.id.container, compositeFragment,"compositeFragment")
                .commit();
    }

    private void moveBackToPreviousFragment(String fieldName,String fields,String value){
        Bundle args = new Bundle();
        args.putString(getString(R.string.label_fields), fields);
        args.putString(getString(R.string.label_fieldname),fieldName);
        args.putString(getString(R.string.label_report),value);
        AddReportFragment compositeFragment = new AddReportFragment();
        compositeFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.enter_rev,R.animator.exit_rev)
                .replace(R.id.container, compositeFragment,"compositeFragment")
                .commit();
    }

    /**
     * to preserve the state on screen rotation.
     * EditText fields reinstated with a counter.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int counter = 0;
        for (int i = 0; i < container.getChildCount(); i++) {
            String viewClass = container.getChildAt(i).getClass().getName();
            if (viewClass.contains("EditText")) {
                EditText et = (EditText) container.getChildAt(i);
                outState.putString(String.valueOf(counter), et.getText().toString());
                counter++;
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int counter = 0;
        for (int i = 0; i < container.getChildCount(); i++) {
            String viewClass = container.getChildAt(i).getClass().getName();
            if (viewClass.contains("EditText")) {
                EditText et = (EditText) container.getChildAt(i);
                et.setText(savedInstanceState.getString(String.valueOf(counter)));
                counter++;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_button_back:
                onBackPressed();
                break;
        }
    }

    private void saveReport(){
        Hubbler.getApp(this).getExecutor().execute(()->{
            Report report = new Report();
            report.setReport(reportObject.toString());
            report.setAddedTime(new Date());
            DatabaseClient.getInstance(this).getAppDatabase().reportDao().save(report);
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OnReportUpdateEvent event) throws JSONException {
        titles.pop();
        bluePrints.pop();
        if(titles.size() > 0){
            pageTitle.setText(String.format(getString(R.string.label_add_a),titles.peek()));

            if(titles.size() > 1){
                JSONObject reportSlice = new JSONObject();
                reportSlice.put(event.getFieldName(),event.getValue());
                reportObject.put(titles.peek(),reportSlice);
                moveBackToPreviousFragment(titles.peek(),bluePrints.peek(),reportSlice.toString());
            }else {
                if(reportObject.has(event.getFieldName())){
                    JSONObject jsonObject = event.getValue();
                    for(int i = 0;i<jsonObject.names().length();i++){
                        reportObject.getJSONObject(event.getFieldName()).put(jsonObject.names().getString(i),jsonObject.get(jsonObject.names().getString(i)));
                    }
                }else {
                    reportObject.put(event.getFieldName(),event.getValue());
                }
                moveBackToPreviousFragment(titles.peek(),bluePrints.peek(),reportObject.toString());
            }
        }else {
            JSONObject jsonObject = event.getValue();
            for(int i = 0;i<jsonObject.names().length();i++){
                reportObject.put(jsonObject.names().getString(i),jsonObject.get(jsonObject.names().getString(i)));
            }
            Log.e("REPORT OBJECT",reportObject.toString());
            saveReport();
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        bluePrints.pop();
        titles.pop();
        if(titles.size() > 0){
            pageTitle.setText(String.format(getString(R.string.label_add_a),titles.peek()));
            moveBackToPreviousFragment(titles.peek(),bluePrints.peek(),reportObject.toString());
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.enter2, R.anim.exit2);
    }
}
