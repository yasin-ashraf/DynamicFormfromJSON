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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_report);
        container = findViewById(R.id.container);
        buttonDone = findViewById(R.id.button_done);
        backButton = findViewById(R.id.iv_button_back);
        reportObject = new JSONObject();

        buttonDone.setOnClickListener(this);
        backButton.setOnClickListener(this);
        readJsonFile();
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
                final String fieldName = viewObject.getString("field-name");
                String type = viewObject.getString("type");

                boolean required = false;
                int min = -1;
                int max = -1;
                ArrayList<String> options = null;

                if (viewObject.has("required")) {
                    required = viewObject.getBoolean("required");
                }
                if (viewObject.has("min")) {
                    min = viewObject.getInt("min");
                }
                if (viewObject.has("max")) {
                    max = viewObject.getInt("max");
                }
                if (viewObject.has("options")) {
                    options = new ArrayList<>();
                    for (int j = 0; j < viewObject.getJSONArray("options").length(); j++) {
                        options.add(viewObject.getJSONArray("options").get(j).toString());
                    }
                }
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(50, 10, 50, 10);

                switch (type) {
                    case "text":
                        container.addView(createTextView(fieldName));

                        EditText editText = new EditText(this);
                        editText.setHint(String.format("Type %s here.", fieldName));
                        editText.setHintTextColor(ContextCompat.getColor(this, R.color.hint));
                        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                        editText.setSingleLine(true);
                        editText.setBackground(ContextCompat.getDrawable(this, android.R.color.transparent));
                        editText.setLayoutParams(layoutParams);

                        //set required tag
                        if (required)
                            editText.setTag("required");

                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                Hubbler.getApp(AddReportActivity.this).getExecutor().execute(()->{
                                    try {
                                        reportObject.put(fieldName, charSequence.toString());
                                        Log.e("REPORTOBJECT",reportObject.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                });
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });


                        container.addView(editText);
                        break;

                    case "number":
                        container.addView(createTextView(fieldName));

                        final EditText editTextNUmber = new EditText(this);
                        editTextNUmber.setHint(String.format("Type %s here.", fieldName));
                        editTextNUmber.setHintTextColor(ContextCompat.getColor(this, R.color.hint));
                        editTextNUmber.setImeOptions(EditorInfo.IME_ACTION_DONE);
                        editTextNUmber.setInputType(InputType.TYPE_CLASS_NUMBER);
                        editTextNUmber.setBackground(ContextCompat.getDrawable(this, android.R.color.transparent));
                        editTextNUmber.setLayoutParams(layoutParams);

                        //set required tag
                        if (required)
                            editTextNUmber.setTag("required");

                        final int minF = min;
                        final int maxF = max;
                        editTextNUmber.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                if (minF != -1 || maxF != -1 && maxF > minF) {
                                    final int a = !charSequence.toString().equals("") ? Integer.parseInt(charSequence.toString()) : 0;
                                    if (minF < a) {
                                        if (a < maxF) {
                                            editTextNUmber.setError(null);
                                            valid = true;
                                        } else {
                                            editTextNUmber.setError(String.format("%s should be less than %s", fieldName, maxF));
                                            valid = false;
                                        }
                                    } else {
                                        editTextNUmber.setError(String.format("%s should be more than %s", fieldName, minF));
                                        valid = false;
                                    }
                                } else {
                                    valid = true;
                                    Hubbler.getApp(AddReportActivity.this).getExecutor().execute(()->{
                                        try {
                                            reportObject.put(fieldName, editTextNUmber.getText().toString());
                                            Log.e("REPORTOBJECT",reportObject.toString());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });


                        container.addView(editTextNUmber);
                        break;

                    case "multiline":
                        //Textview for Label
                        container.addView(createTextView(fieldName));

                        EditText editTextMultiline = new EditText(this);
                        editTextMultiline.setHint(String.format("Type %s here.", fieldName));
                        editTextMultiline.setHintTextColor(ContextCompat.getColor(this, R.color.hint));
                        editTextMultiline.setGravity(Gravity.TOP);
                        editTextMultiline.setSingleLine(false);
                        editTextMultiline.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                        editTextMultiline.setBackground(ContextCompat.getDrawable(this, android.R.color.transparent));
                        editTextMultiline.setMinHeight(300);
                        editTextMultiline.setLayoutParams(layoutParams);

                        //set required tag
                        if (required)
                            editTextMultiline.setTag("required");

                        editTextMultiline.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                Hubbler.getApp(AddReportActivity.this).getExecutor().execute(() ->{
                                    try {
                                        reportObject.put(fieldName, charSequence.toString());
                                        Log.e("REPORTOBJECT",reportObject.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                });
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });

                        container.addView(editTextMultiline);
                        break;

                    case "dropdown":
                        if (options != null) {
                            container.addView(createTextView(fieldName));

                            Spinner spinner = new Spinner(this);
                            LinearLayout.LayoutParams spinnerLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            spinnerLayoutParams.setMargins(50, 25, 50, 10);
                            spinner.setMinimumWidth(700);
                            spinner.setLayoutParams(spinnerLayoutParams);
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, options);
                            spinner.setAdapter(adapter);

                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    Hubbler.getApp(AddReportActivity.this).getExecutor().execute(() ->{
                                        try {
                                            reportObject.put(fieldName,adapterView.getSelectedItem().toString());
                                            Log.e("REPORTOBJECT",reportObject.toString());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    Hubbler.getApp(AddReportActivity.this).getExecutor().execute(() ->{
                                        try {
                                            reportObject.put(fieldName,adapterView.getItemAtPosition(0).toString());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                }
                            });
                            container.addView(spinner);
                        }
                        break;
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
                    //Adding report to db causes unknown issue while adding it from different thread. Hence moving it back to main thread.
                    Report report = new Report();
                    report.setReport(reportObject.toString());
                    report.setAddedTime(new Date());
                    DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().reportDao().save(report);
                    Toast.makeText(this, "Report Added", Toast.LENGTH_SHORT).show();
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
                if (et.getTag() != null && et.getTag().toString().contains("required")) {
                    if (et.getText().toString().trim().isEmpty()) {
                        et.setError("This field is required.");
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
        LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewLayoutParams.setMargins(50, 25, 50, 10);

        TextView textView = new TextView(this);
        textView.setText(String.format("%s :", fieldName));
        textView.setTextSize(16);
        textView.setLayoutParams(textViewLayoutParams);
        return textView;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.enter2, R.anim.exit2);
    }
}
