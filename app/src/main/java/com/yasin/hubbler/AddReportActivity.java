package com.yasin.hubbler;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by im_yasinashraf started on 1/11/18.
 */
public class AddReportActivity extends AppCompatActivity implements View.OnClickListener {

    private String jsonData;
    private LinearLayout container;
    private List<String> editTextTags = new ArrayList<>();
    private List<String> inputLayoutTags = new ArrayList<>();
    private FrameLayout buttonDone;
    private static final int FLAG_PHONE = 1;
    private static final int FLAG_PIN_CODE = 1 << 2;
    private int mErrorFlags;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_report);
        container = findViewById(R.id.container);
        buttonDone = findViewById(R.id.button_done);

        buttonDone.setOnClickListener(this);
        readJsonFile();
    }

    private void readJsonFile() {
        try {
            InputStream inputStream = this.getAssets().open("file.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            jsonData = new String(buffer, "UTF-8");

            parseJsonData(jsonData);

            Log.e("data", jsonData);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void parseJsonData(String jsonData){ // do in different thread

        try {
            JSONArray viewArray = new JSONArray(jsonData);

            for (int i = 0; i < viewArray.length(); i++) { // for-each not applicable to jsonArray
                JSONObject viewObject = viewArray.getJSONObject(i);
                String fieldName = viewObject.getString("field-name");
                String type = viewObject.getString("type");
                boolean required;
                if(viewObject.has("required")){
                    required = viewObject.getBoolean("required");
                }

                //Layout Params
                Resources r = getResources();
                float leftMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());
                float rightMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());
                float topMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics());
                float bottomMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());

                LinearLayout.LayoutParams layoutParams =  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(50,50,50,50);

                switch (type){
                    case "text":
                        TextInputLayout textInputLayout = new TextInputLayout(this);
                        TextInputEditText textInputEditText = new TextInputEditText(this);

                        textInputEditText.setHint(fieldName);
                        textInputEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                        textInputLayout.setBackground(ContextCompat.getDrawable(this,R.drawable.edge));
                        LinearLayout.LayoutParams textInputLayoutParams =  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        textInputLayoutParams.setMargins(0,10,0,10);
                        textInputEditText.setLayoutParams(textInputLayoutParams);
                        //set Tag
                        textInputEditText.setTag(fieldName + "et");
                        editTextTags.add(fieldName + "et");
                        textInputLayout.addView(textInputEditText);
                        textInputLayout.setTag(fieldName + "il");
                        inputLayoutTags.add(fieldName + "il");
                        // add layout params
                        textInputLayout.setLayoutParams(layoutParams);

                        container.addView(textInputLayout);
                        break;

                    case "number":
                        LinearLayout linearLayout = new LinearLayout(this);
                        linearLayout.setOrientation(LinearLayout.VERTICAL);
                        linearLayout.setBackground(ContextCompat.getDrawable(this,R.drawable.edge));
                        linearLayout.setLayoutParams(layoutParams);
                        // Textview for label
                        TextView textView = new TextView(this);
                        textView.setText(fieldName);
                        LinearLayout.LayoutParams textViewLayoutParams =  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        textViewLayoutParams.setMargins(10,10,10,10);
                        textView.setLayoutParams(textViewLayoutParams);
                        linearLayout.addView(textView);

                        //Number Picker for Number
                        NumberPicker numberPicker = new NumberPicker(this);
                        if(viewObject.has("min")) numberPicker.setMinValue(viewObject.getInt("min"));
                        else numberPicker.setMinValue(0);
                        if(viewObject.has("max")) numberPicker.setMaxValue(viewObject.getInt("max"));
                        LinearLayout.LayoutParams pickerLayoutParams =  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        pickerLayoutParams.setMargins(100,50,100,50);
                        numberPicker.setLayoutParams(pickerLayoutParams);
                        linearLayout.addView(numberPicker);

                        container.addView(linearLayout);
                        break;

                    case "multiline":
                        TextInputLayout multiLineTextInputLayout = new TextInputLayout(this);
                        TextInputEditText multiLineEditText = new TextInputEditText(this);
                        multiLineEditText.setHint(fieldName);
                        multiLineEditText.setSingleLine(false);
                        multiLineEditText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                        multiLineEditText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);

                        LinearLayout.LayoutParams multilineLayoutParams =  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 350);
//                        multilineLayoutParams.setMargins(0,10,0,10);
                        multiLineEditText.setLayoutParams(multilineLayoutParams);
                        multiLineTextInputLayout.setBackground(ContextCompat.getDrawable(this,R.drawable.edge));
                        multiLineTextInputLayout.addView(multiLineEditText);

                        // add specific layout params
                        multiLineTextInputLayout.setLayoutParams(layoutParams);

                        container.addView(multiLineTextInputLayout);
                        break;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_done:
                if(!ensureValidated()){

                }
                break;
        }
    }

    private boolean ensureValidated() {
        validateEditTexts();
        //validatePinCode();
        return mErrorFlags == 0;
    }

    private void validateEditTexts() {
        for(int i=0;i<editTextTags.size();i++){
            EditText editText = container.findViewWithTag(editTextTags.get(i));
            String value = editText.getText().toString().trim();
            if (value.equals("")) {
                mErrorFlags |= FLAG_PIN_CODE;
                TextInputLayout textInputLayout = container.findViewWithTag(inputLayoutTags.get(i));
                textInputLayout.setError(getString(R.string.error_blank));
            } else {
                mErrorFlags &= ~FLAG_PIN_CODE;
            }
        }
    }

    private void validatePinCode(){
        /*String value = etMobileNumber.getText().toString().trim();
        if (Patterns.PHONE.matcher(value).matches()) {
            mErrorFlags &= ~FLAG_PHONE;
        } else {
            mErrorFlags |= FLAG_PHONE;
            mobileNumberLayout.setError(getString(R.string.error_invalid_phone));
        }*/
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.enter2, R.anim.exit2);
    }
}
