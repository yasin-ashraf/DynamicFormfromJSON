package com.yasin.hubbler.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.yasin.hubbler.Activity.ViewReportActivity;
import com.yasin.hubbler.DatabaseClient;
import com.yasin.hubbler.Hubbler;
import com.yasin.hubbler.Model.Report;
import com.yasin.hubbler.R;
import com.yasin.hubbler.Validators.EmailValidator;
import com.yasin.hubbler.Validators.NumberValidator;
import com.yasin.hubbler.ViewGenerators.CompositeBoxViewGenerator;
import com.yasin.hubbler.ViewGenerators.EditTextGenerator;
import com.yasin.hubbler.ViewGenerators.SpinnerGenerator;
import com.yasin.hubbler.ViewGenerators.TextViewGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * Created by im_yasinashraf started on 16/11/18.
 */
public class EditReportFragment extends Fragment implements View.OnClickListener {

    private EmailValidator emailValidator;
    private NumberValidator numberValidator;
    private EditTextGenerator editTextGenerator;
    private TextViewGenerator textViewGenerator;
    private SpinnerGenerator spinnerGenerator;
    private CompositeBoxViewGenerator compositeBoxViewGenerator;
    private LinearLayout containerView;
    private FrameLayout updateButton;
    private JSONObject reportObject;
    private Boolean valid = false;
    private Integer id;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = Objects.requireNonNull(getArguments()).getInt("id");
        emailValidator = new EmailValidator();
        numberValidator = new NumberValidator();
        editTextGenerator = new EditTextGenerator(getActivity());
        textViewGenerator = new TextViewGenerator(getActivity());
        spinnerGenerator = new SpinnerGenerator(getActivity());
        compositeBoxViewGenerator = new CompositeBoxViewGenerator(getActivity());
    }

    private void initReportObject(String report) {
        try {
            reportObject = new JSONObject(report);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_report,container,false);
        initViews(view);
        if(getArguments().getBoolean("isComposite")){
            initReportObject(getArguments().getString("value"));
            parseJsonData(getArguments().getString("fields"));
        }else {
            String report = ((ViewReportActivity)Objects.requireNonNull(getActivity())).getReport();
            initReportObject(report);
            readJsonFile();
        }

        return view;
    }

    private void initViews(View view){
        containerView = view.findViewById(R.id.container);
        updateButton = view.findViewById(R.id.button_update_report);

        updateButton.setOnClickListener(this);
    }

    private void readJsonFile() {
        try {
            InputStream inputStream = Objects.requireNonNull(getActivity()).getAssets().open("file.json");
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
     * Parse JSON file again and create views accordingly.
     * get appropriate saved values from saved json string
     */
    private void parseJsonData(String jsonData) {
        try {
            JSONArray viewArray = new JSONArray(jsonData);

            for (int i = 0; i < viewArray.length(); i++) {
                JSONObject viewObject = viewArray.getJSONObject(i);
                final String fieldName = viewObject.getString(getString(R.string.label_field_name));
                String type = viewObject.getString(getString(R.string.label_type));

                boolean required = false;
                int min = -1,max = -1;
                String compositeFields = "";
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
                if (viewObject.has(getString(R.string.label_fields))){
                    compositeFields = viewObject.getJSONArray(getString(R.string.label_fields)).toString();
                }
                String value = reportObject.getString(fieldName);
                createViews(type,fieldName,options,required,value,min,max,compositeFields);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createViews(String type,String fieldName,ArrayList<String> options,Boolean required,String value,int min,int max, String compositeFields){// too much parameters??
        containerView.addView(createTextView(fieldName));
        if(type.equals(getString(R.string.label_dropdown))){
            if (options != null) {
                containerView.addView(createSpinner(fieldName,options,value));
            }
        }else if(type.equals(getString(R.string.label_composite))) {
            containerView.addView(createBoxView(fieldName, compositeFields,value));
        }else {
            containerView.addView(createEditText(type,fieldName,required,min,max,value));
        }
    }

    private TextView createTextView(String fieldName) {
        return textViewGenerator.generateTextView(fieldName);
    }

    private EditText createEditText(String type, String fieldName, Boolean required, int min, int max, String value){// too much parameters??
        EditText editText = editTextGenerator.generateEditText(type, fieldName, required, min, max);
        editText.setText(value);
        addTextChangedListener(editText,type,min,max);
        return editText;
    }

    private LinearLayout createBoxView(String fieldName,String compositeFields, String value){
        LinearLayout linearLayout = null;
        try {
            JSONObject jsonObject = new JSONObject(value);
            linearLayout = compositeBoxViewGenerator.createBoxViewWithTypedValues(jsonObject);
            linearLayout.setOnClickListener(view -> {
                ((ViewReportActivity)getActivity()).replaceWithCompositeFragment(value,compositeFields);
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return linearLayout;
    }


    private Spinner createSpinner(String fieldName, ArrayList<String> options,String value) {
        Spinner spinner = spinnerGenerator.generateSpinner(options);
        spinner.setSelection(options.indexOf(value));
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

    /**
     * Checking if the required fields are empty.
     */
    private void validateEditTexts() {
        for (int i = 0; i < containerView.getChildCount(); i++) {
            String viewClass = containerView.getChildAt(i).getClass().getName();
            if (viewClass.contains("EditText")) {
                EditText et = (EditText) containerView.getChildAt(i);
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

    private boolean ensureValidated() {
        validateEditTexts();
        return valid;
    }

    private void createReportObject() {
        for (int i = 0; i < containerView.getChildCount(); i++) {
            String viewClass = containerView.getChildAt(i).getClass().getName();
            if (viewClass.contains("EditText")) {
                EditText et = (EditText) containerView.getChildAt(i);
                if (et.getTag() != null && et.getTag().toString().contains(getString(R.string.label_required))) {
                    String[] data = et.getTag().toString().split(";");
                    try {
                        reportObject.put(data[1],et.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    try {
                        reportObject.put(et.getTag().toString(),et.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void updateReport(){
        Hubbler.getApp(Objects.requireNonNull(getActivity())).getExecutor().execute(()->{
            Report report = new Report();
            report.setReport(reportObject.toString());
            report.setId(id);
            report.setAddedTime(new Date());
            DatabaseClient.getInstance(getActivity().getApplicationContext()).getAppDatabase().reportDao().update(report); //update Report in DB
            ((ViewReportActivity)Objects.requireNonNull(getActivity())).setReport(report.getReport()); // Update Report Object in Activity
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_update_report:
                if(ensureValidated()){
                    if(getArguments().getBoolean("isComposite")){

                    }else {
                        createReportObject();
                        updateReport();
                        getActivity().onBackPressed();
                    }
                }
                break;
        }
    }
}
