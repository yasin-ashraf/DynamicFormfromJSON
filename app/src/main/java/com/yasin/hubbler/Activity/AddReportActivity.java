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

import com.yasin.hubbler.Fragment.AddReportFragment;
import com.yasin.hubbler.R;
import com.yasin.hubbler.ReportObjectListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by im_yasinashraf started on 1/11/18.
 */
public class AddReportActivity extends AppCompatActivity implements View.OnClickListener,ReportObjectListener {

    private FrameLayout container;
    private ImageView backButton;
    private TextView pageTitle;
    private JSONObject reportObject;
    private boolean isInComposite = false;
    private Map<String,JSONObject> filledCompositeFields;
    private Map<String,String> filledFields;
    private Stack<String> titles;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_report);
        reportObject = new JSONObject();
        filledCompositeFields = new HashMap<>();
        filledFields = new HashMap<>();
        titles = new Stack<>();
        init();
        readJsonFile();
    }

    private void init(){
        container = findViewById(R.id.container);
        backButton = findViewById(R.id.iv_button_back);
        pageTitle = findViewById(R.id.label_add_a_report);

        backButton.setOnClickListener(this);
    }

    public JSONObject getReportObject() {
        return reportObject;
    }

    public boolean isInComposite() {
        return isInComposite;
    }

    public void setFilledCompositeField(String fieldName,JSONObject jsonObject){
        filledCompositeFields.put(fieldName,jsonObject);
    }

    public Map<String, JSONObject> getFilledCompositeFields() {
        return filledCompositeFields;
    }

    public void setFilledOtherField(String fieldName,String value){
        filledFields.put(fieldName,value);
    }

    public Map<String, String> getFilledFields() {
        return filledFields;
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
        Bundle args = new Bundle();
        args.putString(getString(R.string.label_fields),json);
        AddReportFragment addReportFragment = new AddReportFragment();
        addReportFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fade_in,R.animator.fade_out)
                .replace(R.id.container, addReportFragment,"addReportFragment")
                .commit();
    }

    public void replaceWithCompositeFragment(String fieldName, String compositeFields){
        pageTitle.setText(String.format(getString(R.string.label_add_a),fieldName));
        titles.push(fieldName);
        isInComposite = true;
        Bundle args = new Bundle();
        args.putString(getString(R.string.label_fields),compositeFields);
        args.putString(getString(R.string.label_fieldname),fieldName);
        AddReportFragment compositeFragment = new AddReportFragment();
        compositeFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.enter,R.animator.exit,R.animator.enter_rev,R.animator.exit_rev)
                .replace(R.id.container, compositeFragment,"compositeFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onUpdateReport(String report, String fieldName) {
        try {
            JSONObject reportSlice = new JSONObject(report);
            getReportObject().put(fieldName,reportSlice);
            Log.e("REPORT OBJECT",getReportObject().toString());
            onBackPressed();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateFinalReport(String key, String value) {
        try {
            getReportObject().put(key,value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 1){
            getSupportFragmentManager().popBackStack();
            titles.pop();
            pageTitle.setText(String.format(getString(R.string.label_add_a),titles.peek()));
        }else if(getSupportFragmentManager().getBackStackEntryCount() == 1){
            isInComposite = false;
            pageTitle.setText(getString(R.string.label_add_a_report));
            getSupportFragmentManager().popBackStack();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.enter2, R.anim.exit2);
    }
}
