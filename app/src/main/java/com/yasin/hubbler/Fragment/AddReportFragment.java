package com.yasin.hubbler.Fragment;

import android.content.Context;
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
import android.widget.Toast;

import com.yasin.hubbler.Activity.AddReportActivity;
import com.yasin.hubbler.DatabaseClient;
import com.yasin.hubbler.Hubbler;
import com.yasin.hubbler.Model.Report;
import com.yasin.hubbler.R;
import com.yasin.hubbler.ReportObjectListener;
import com.yasin.hubbler.Validators.EmailValidator;
import com.yasin.hubbler.Validators.NumberValidator;
import com.yasin.hubbler.ViewGenerators.CompositeBoxViewGenerator;
import com.yasin.hubbler.ViewGenerators.EditTextGenerator;
import com.yasin.hubbler.ViewGenerators.SpinnerGenerator;
import com.yasin.hubbler.ViewGenerators.TextViewGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * Created by im_yasinashraf started on 18/11/18.
 */
public class AddReportFragment extends Fragment implements View.OnClickListener {

    private LinearLayout container;
    private FrameLayout buttonDone;
    private EmailValidator emailValidator;
    private NumberValidator numberValidator;
    private EditTextGenerator editTextGenerator;
    private TextViewGenerator textViewGenerator;
    private SpinnerGenerator spinnerGenerator;
    private CompositeBoxViewGenerator compositeBoxViewGenerator;
    private ReportObjectListener reportObjectListener;
    private Boolean valid = false;
    private JSONObject reportObjectSlice;
    private String compositeFieldName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reportObjectSlice = new JSONObject();
        emailValidator = new EmailValidator();
        numberValidator = new NumberValidator();
        editTextGenerator = new EditTextGenerator(getActivity());
        textViewGenerator = new TextViewGenerator(getActivity());
        spinnerGenerator = new SpinnerGenerator(getActivity());
        compositeBoxViewGenerator = new CompositeBoxViewGenerator(getActivity());
        compositeFieldName = getArguments().getString("fieldName");
    }

    @Override
    public void onAttach(Context context) {
        reportObjectListener = (ReportObjectListener) context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_report, container, false);
        initViews(view);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        parseJsonData(Objects.requireNonNull(getArguments()).getString("fields"));
    }

    private void initViews(View view) {
        container = view.findViewById(R.id.container);
        buttonDone = view.findViewById(R.id.button_done);

        buttonDone.setOnClickListener(this);
    }


    /**
     * Parse JSON file and create views accordingly.
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
                generateViews(type,fieldName,options,required,min,max,compositeFields);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void generateViews(String type,String fieldName,ArrayList<String> options,Boolean required,int min,int max,String compositeFields){// too much parameters??
        container.addView(createTextView(fieldName));
        if(type.equals(getString(R.string.label_dropdown))){
            if (options != null) {
                container.addView(createSpinner(fieldName,options));
            }
        }else if(type.equals(getString(R.string.label_composite))) {
            container.addView(createBoxView(fieldName, compositeFields));
        }else {
            container.addView(createEditText(type,fieldName,required,min,max));
        }
    }

    private void saveReport(){
        Hubbler.getApp(Objects.requireNonNull(getActivity())).getExecutor().execute(()->{
            Report report = new Report();
            report.setReport(((AddReportActivity)getActivity()).getReportObject().toString());
            report.setAddedTime(new Date());
            DatabaseClient.getInstance(getActivity()).getAppDatabase().reportDao().save(report);
        });
    }

    private void createReportObjectSlice() {
        for (int i = 0; i < container.getChildCount(); i++) {
            String viewClass = container.getChildAt(i).getClass().getName();
            if (viewClass.contains("EditText")) {
                EditText et = (EditText) container.getChildAt(i);
                if (et.getTag() != null && et.getTag().toString().contains(getString(R.string.label_required))) {
                    String[] data = et.getTag().toString().split(";");
                    try {
                        reportObjectSlice.put(data[1],et.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    try {
                        reportObjectSlice.put(et.getTag().toString(),et.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void createFinalReportObject() {
        for (int i = 0; i < container.getChildCount(); i++) {
            String viewClass = container.getChildAt(i).getClass().getName();
            if (viewClass.contains("EditText")) {
                EditText et = (EditText) container.getChildAt(i);
                if (et.getTag() != null && et.getTag().toString().contains(getString(R.string.label_required))) {
                    String[] data = et.getTag().toString().split(";");
                    reportObjectListener.onCreateFinalReport(data[1],et.getText().toString());
                }else {
                    reportObjectListener.onCreateFinalReport(et.getTag().toString(),et.getText().toString());
                }
            }
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

    private LinearLayout createBoxView(String fieldName,String compositeFields){
        if(((AddReportActivity)Objects.requireNonNull(getActivity())).getFilledCompositeFields().containsKey(fieldName)){
            LinearLayout linearLayout = compositeBoxViewGenerator.createBoxViewWithTypedValues(((AddReportActivity)Objects.requireNonNull(getActivity())).getFilledCompositeFields().get(fieldName));
            linearLayout.setOnClickListener(view -> {
                ((AddReportActivity)Objects.requireNonNull(getActivity())).replaceWithCompositeFragment(fieldName,compositeFields);
            });
            return linearLayout;
        }else {
            LinearLayout linearLayout = compositeBoxViewGenerator.createBoxView(fieldName);
            linearLayout.setOnClickListener(view -> {
                ((AddReportActivity)Objects.requireNonNull(getActivity())).replaceWithCompositeFragment(fieldName,compositeFields);
            });
            return linearLayout;
        }
    }

    private TextView createTextView(String fieldName) {
        return textViewGenerator.generateTextView(fieldName);
    }

    private EditText createEditText(String type, String fieldName, Boolean required,int min,int max){// too much parameters??
        EditText editText = editTextGenerator.generateEditText(type, fieldName, required, min, max);
        addTextChangedListener(editText,type,min,max,fieldName);
        if(((AddReportActivity)Objects.requireNonNull(getActivity())).getFilledFields().containsKey(fieldName)){
            editText.setText(((AddReportActivity)Objects.requireNonNull(getActivity())).getFilledFields().get(fieldName));
        }
        return editText;
    }

    private Spinner createSpinner(String fieldName, ArrayList<String> options) {
        Spinner spinner = spinnerGenerator.generateSpinner(options);
        if(((AddReportActivity)Objects.requireNonNull(getActivity())).getFilledFields().containsKey(fieldName)){
            spinner.setSelection(options.indexOf(((AddReportActivity)Objects.requireNonNull(getActivity())).getFilledFields().get(fieldName)));
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                reportObjectListener.onCreateFinalReport(fieldName,adapterView.getSelectedItem().toString());
                ((AddReportActivity)Objects.requireNonNull(getActivity())).setFilledOtherField(fieldName,adapterView.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                reportObjectListener.onCreateFinalReport(fieldName,adapterView.getItemAtPosition(0).toString());
                ((AddReportActivity)Objects.requireNonNull(getActivity())).setFilledOtherField(fieldName,adapterView.getItemAtPosition(0).toString());
            }
        });
        return spinner;
    }

    private void addTextChangedListener(EditText editText,String type,int min,int max,String fieldName){
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
                            ((AddReportActivity)Objects.requireNonNull(getActivity())).setFilledOtherField(fieldName,editText.getText().toString());
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
                                ((AddReportActivity)Objects.requireNonNull(getActivity())).setFilledOtherField(fieldName,editText.getText().toString());
                            }else {
                                editText.setError(String.format(getString(R.string.label_should_be_between),min,max));
                                valid = false;
                            }
                        } else {
                            valid = true;
                        }
                        break;

                     default:
                        ((AddReportActivity)Objects.requireNonNull(getActivity())).setFilledOtherField(fieldName,editText.getText().toString());
                        break;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_done:
                if (ensureValidated()) {
                    if(((AddReportActivity)getActivity()).isInComposite()){
                        createReportObjectSlice();
                        ((AddReportActivity)getActivity()).setFilledCompositeField(compositeFieldName,reportObjectSlice);
                        reportObjectListener.onUpdateReport(reportObjectSlice.toString(),compositeFieldName);
                    }else {
                        createFinalReportObject();
                        saveReport();
                        Log.e("REPORT OBJECT",((AddReportActivity) getActivity()).getReportObject().toString());
                        Toast.makeText(getActivity(), R.string.label_report_added, Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                }
                break;
        }
    }
}
