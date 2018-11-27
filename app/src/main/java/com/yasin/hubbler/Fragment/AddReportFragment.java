package com.yasin.hubbler.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.yasin.hubbler.Activity.AddReportActivity;
import com.yasin.hubbler.EventBus.OnReportUpdateEvent;
import com.yasin.hubbler.R;
import com.yasin.hubbler.Validators.EmailValidator;
import com.yasin.hubbler.Validators.NumberValidator;
import com.yasin.hubbler.ViewGenerators.CompositeBoxViewGenerator;
import com.yasin.hubbler.ViewGenerators.EditTextGenerator;
import com.yasin.hubbler.ViewGenerators.SpinnerGenerator;
import com.yasin.hubbler.ViewGenerators.TextViewGenerator;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    private Boolean valid = false;
    private JSONObject reportObjectSlice;
    private String compositeFieldName;
    private JSONObject report;

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
        compositeFieldName = getArguments().getString(getString(R.string.label_fieldname));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_report, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        container = view.findViewById(R.id.container);
        buttonDone = view.findViewById(R.id.button_done);

        buttonDone.setOnClickListener(this);
        try {
            report = new JSONObject(getArguments().getString(getString(R.string.label_report)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        parseJsonData(Objects.requireNonNull(getArguments()).getString(getString(R.string.label_fields)));
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
                String value = "";
                if(report.has(fieldName)){
                    value = report.getString(fieldName);
                }
                createViews(type,fieldName,options,required,min,max,compositeFields,value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createViews(String type,String fieldName,ArrayList<String> options,Boolean required,int min,int max, String compositeFields,String value){// too much parameters??
        container.addView(createTextView(fieldName));
        if(type.equals(getString(R.string.label_dropdown))){
            if (options != null) {
                container.addView(createSpinner(fieldName,options,value));
            }
        }else if(type.equals(getString(R.string.label_composite))) {
            container.addView(createBoxView(fieldName, compositeFields,value));
        }else {
            container.addView(createEditText(type,fieldName,required,min,max,value));
        }
    }

    private void createReportObjectSlice() {
        for (int i = 0; i < container.getChildCount(); i++) {
            String viewClass = container.getChildAt(i).getClass().getName();
            if (viewClass.contains("EditText")) {
                EditText et = (EditText) container.getChildAt(i);
                String[] data = et.getTag().toString().split(";");
                try {
                    reportObjectSlice.put(data[0],et.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
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

    private TextView createTextView(String fieldName) {
        return textViewGenerator.generateTextView(fieldName);
    }

    private EditText createEditText(String type, String fieldName, Boolean required, int min, int max, String value){
        EditText editText = editTextGenerator.generateEditText(type, fieldName, required,compositeFieldName);
        setEditFieldText(editText,value);
        addEditorActionListener(editText);
        addFocusChangeListener(editText);
        addTextChangedListener(editText,type,min,max);
        return editText;
    }

    private LinearLayout createBoxView(String fieldName,String compositeFields, String value){
        LinearLayout linearLayout = null;
        if(!value.equals("")){
            try {
                JSONObject jsonObject = new JSONObject(value);
                linearLayout = compositeBoxViewGenerator.createBoxViewWithTypedValues(jsonObject);
                linearLayout.setOnClickListener(view -> {
                    ((AddReportActivity)getActivity()).replaceWithCompositeFragment(fieldName,compositeFields,value);
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            linearLayout = compositeBoxViewGenerator.createBoxView(fieldName);
            linearLayout.setOnClickListener(view -> {
                ((AddReportActivity)getActivity()).replaceWithCompositeFragment(fieldName,compositeFields,value);
            });
        }

        return linearLayout;
    }


    private Spinner createSpinner(String fieldName, ArrayList<String> options,String value) {
        Spinner spinner = spinnerGenerator.generateSpinner(options);
        setSelectionOfSpinner(spinner,fieldName,options,value);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    reportObjectSlice.put(fieldName,adapterView.getSelectedItem().toString());
                    ((AddReportActivity)getActivity()).setFilledFields(fieldName,adapterView.getSelectedItem().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                try {
                    reportObjectSlice.put(fieldName,adapterView.getItemAtPosition(0).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return spinner;
    }

    private void setSelectionOfSpinner(Spinner spinner, String fieldName, ArrayList<String> options, String value){
        if(value.equals("")){
            if(((AddReportActivity)getActivity()).getFilledFields().containsKey(fieldName)){
                spinner.setSelection(options.indexOf(((AddReportActivity)getActivity()).getFilledFields().get(fieldName)));
            }
        }else {
            spinner.setSelection(options.indexOf(value));
        }
    }

    private void setEditFieldText(EditText editText,String value){
        if(value.equals("")){
            if(((AddReportActivity)getActivity()).getFilledFields().containsKey(editText.getTag().toString())){
                editText.setText(((AddReportActivity)getActivity()).getFilledFields().get(editText.getTag().toString()));
            }
        }else {
            editText.setText(value);
        }
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

                     default:
                        break;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void addEditorActionListener(EditText editText){
        editText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(!editText.getText().toString().equals("")){
                ((AddReportActivity)getActivity()).setFilledFields(editText.getTag().toString(),editText.getText().toString());
            }
            return false;
        });
    }

    private void addFocusChangeListener(EditText editText) {
        editText.setOnFocusChangeListener((view, b) -> {
            if(!view.hasFocus()){
                if(!editText.getText().toString().equals("")){
                    ((AddReportActivity)getActivity()).setFilledFields(editText.getTag().toString(),editText.getText().toString());
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_done:
                if(ensureValidated()){
                    createReportObjectSlice();
                    EventBus.getDefault().post(new OnReportUpdateEvent(compositeFieldName,reportObjectSlice));
                }
                break;
        }
    }
}
