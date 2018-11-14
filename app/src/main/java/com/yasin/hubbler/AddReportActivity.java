package com.yasin.hubbler;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yasin.hubbler.Model.Report;
import com.yasin.hubbler.Validators.EmailValidator;
import com.yasin.hubbler.Validators.NumberValidator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by im_yasinashraf started on 1/11/18.
 */
public class AddReportActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout container;
    private FrameLayout buttonDone;
    private Boolean valid = false;
    private ImageView backButton;
    private JSONObject reportObject;
    private EmailValidator emailValidator;
    private NumberValidator numberValidator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_report);
        init();
        readJsonFile();
    }

    private void init(){
        container = findViewById(R.id.container);
        buttonDone = findViewById(R.id.button_done);
        backButton = findViewById(R.id.iv_button_back);
        reportObject = new JSONObject();
        emailValidator = new EmailValidator();
        numberValidator = new NumberValidator();

        buttonDone.setOnClickListener(this);
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

            parseJsonData(jsonData);

            Log.e("data", jsonData);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Parse JSON file and create views accordingly.Set tag 'required' on views required.
     * Validate min and max on Text Change.
     * Add field values to report JSON Object on text change for EditText and on ItemSelected for Spinner.
     */
    private void parseJsonData(String jsonData) {
        try {
            JSONArray viewArray = new JSONArray(jsonData);

            for (int i = 0; i < viewArray.length(); i++) { // for-each not applicable to jsonArray
                JSONObject viewObject = viewArray.getJSONObject(i);
                final String fieldName = viewObject.getString(getString(R.string.label_field_name));
                String type = viewObject.getString(getString(R.string.label_type));

                boolean required = false;
                int min = -1,max = -1;
                ArrayList<String> options = null;
                if (viewObject.has(getString(R.string.label_required))) required = viewObject.getBoolean(getString(R.string.label_required));
                if (viewObject.has(getString(R.string.label_min))) min = viewObject.getInt(getString(R.string.label_min));
                if (viewObject.has(getString(R.string.label_max))) max = viewObject.getInt(getString(R.string.label_max));
                if (viewObject.has(getString(R.string.label_options))) {
                    options = new ArrayList<>();
                    for (int j = 0; j < viewObject.getJSONArray(getString(R.string.label_options)).length(); j++) {
                        options.add(viewObject.getJSONArray(getString(R.string.label_options)).get(j).toString());
                    }
                }
                container.addView(createTextView(fieldName));
                if(type.equals(getString(R.string.label_dropdown))){
                    if (options != null) {
                        container.addView(createSpinner(fieldName,options));
                    }
                }else {
                    container.addView(createEditText(type,fieldName,required,min,max));
                }
            }
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
            case R.id.button_done:
                if (ensureValidated()) {
                    Hubbler.getApp(this).getExecutor().execute(()->{
                        Report report = new Report();
                        report.setReport(reportObject.toString());
                        report.setAddedTime(new Date());
                        DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().reportDao().save(report);
                    });

                    Toast.makeText(this, R.string.label_report_added, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

            case R.id.iv_button_back:
                finish();
                break;
        }
    }

    private boolean ensureValidated() {
        validateEditTexts();
        return valid;
    }

    /**
     * Checking if the required fields are empty.
     */
    private void validateEditTexts() {
        for (int i = 0; i < container.getChildCount(); i++) {
            String viewClass = container.getChildAt(i).getClass().getName();
            if (viewClass.contains("EditText")) {
                EditText et = (EditText) container.getChildAt(i);
                if (et.getTag() != null && et.getTag().toString().contains(getString(R.string.label_required))) {
                    if (et.getText().toString().trim().isEmpty()) {
                        et.setError(getString(R.string.label_is_required));
                        valid = false;
                    } else {
                        et.setError(null);
                        valid = true;
                    }
                }
            }
        }
    }

    private TextView createTextView(String fieldName) {
        TextView textView = new TextView(this);
        textView.setText(String.format("%s :", fieldName));
        textView.setTextSize(16);
        textView.setLayoutParams(getGeneralLayoutParams());
        return textView;
    }

    private EditText createEditText(String type, String fieldName, Boolean required,int min,int max){
        EditText editText = new EditText(this);
        editText.setHint(String.format(getString(R.string.label_type_here), fieldName));
        editText.setHintTextColor(ContextCompat.getColor(this, R.color.hint));
        editText.setBackground(ContextCompat.getDrawable(this, android.R.color.transparent));
        if(type.equals(getString(R.string.label_multiline))){
            editText.setGravity(Gravity.TOP);
            editText.setSingleLine(false);
            editText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
            editText.setMinHeight(300);
        }else {
            editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            editText.setSingleLine(true);
        }
        if(type.equals(getString(R.string.label_email))){
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }else if(type.equals(getString(R.string.label_number))){
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        editText.setLayoutParams(getEditFieldLayoutParams());
        if (required) editText.setTag(getString(R.string.label_required));//set required tag
        addTextChangedListener(editText,type,min,max);
        return editText;
    }

    private Spinner createSpinner(String fieldName,ArrayList<String> options) {
        Spinner spinner = new Spinner(this);
        spinner.setMinimumWidth(700);
        spinner.setLayoutParams(getGeneralLayoutParams());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, options);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    reportObject.put(fieldName,adapterView.getSelectedItem().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                try {
                    reportObject.put(fieldName,adapterView.getItemAtPosition(0).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return spinner;
    }

    private void addTextChangedListener(EditText editText,String type,int min,int max){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                switch (type){
                    case "email":
                        if(emailValidator.isValid(editText.getText().toString())){
                            editText.setError(null);
                            valid = true;
                        }else {
                            editText.setError(getString(R.string.label_valid_email));
                            valid = false;
                        }
                        break;

                    case "number":
                        if (min != -1 || max != -1 && max > min) {
                            if(numberValidator.isValid(charSequence,min,max)){
                                editText.setError(null);
                                valid = true;
                            }else {
                                editText.setError(String.format(getString(R.string.label_should_be_between),min,max));
                                valid = false;
                            }
                        } else {
                            valid = true;
                        }
                        break;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private LinearLayout.LayoutParams getGeneralLayoutParams(){
        LinearLayout.LayoutParams generalLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        generalLayoutParams.setMargins(50, 25, 50, 10);
        return generalLayoutParams;
    }

    private LinearLayout.LayoutParams getEditFieldLayoutParams(){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(50, 10, 50, 10);
        return layoutParams;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.enter2, R.anim.exit2);
    }
}
