package com.yasin.hubbler;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by im_yasinashraf started on 1/11/18.
 */
public class AddReportActivity extends AppCompatActivity implements View.OnClickListener {

    private String jsonData;
    private LinearLayout container;
    private List<String> editTextTags = new ArrayList<>();
    private List<String> inputLayoutTags = new ArrayList<>();
    private Map<String,Boolean> viewRequiredMap = new HashMap<>();
    private FrameLayout buttonDone;
    private Boolean valid = false;

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
                boolean required = false;
                if(viewObject.has("required")){
                    required = viewObject.getBoolean("required");
                }

                LinearLayout.LayoutParams layoutParams =  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(50,10,50,10);

                switch (type){
                    case "text":
                        //Textview for Label
                        TextView textView = new TextView(this);
                        textView.setText(String.format("%s :", fieldName));
                        textView.setTextSize(16);
                        LinearLayout.LayoutParams textViewLayoutParams =  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        textViewLayoutParams.setMargins(50,25,50,10);
                        textView.setLayoutParams(textViewLayoutParams);

                        EditText editText = new EditText(this);
                        editText.setHint(String.format("Type %s here.",fieldName));
                        editText.setHintTextColor(ContextCompat.getColor(this,R.color.hint));
                        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                        editText.setBackground(ContextCompat.getDrawable(this,android.R.color.transparent));
                        editText.setLayoutParams(layoutParams);

                        //set Tags
                        if(required)
                        editText.setTag("required");

                        container.addView(textView);
                        container.addView(editText);
                        break;

                    case "number":
                        //Textview for Label
                        TextView textViewNUmber = new TextView(this);
                        textViewNUmber.setText(String.format("%s :", fieldName));
                        textViewNUmber.setTextSize(16);
                        LinearLayout.LayoutParams textViewNumberLayoutParams =  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        textViewNumberLayoutParams.setMargins(50,25,50,10);
                        textViewNUmber.setLayoutParams(textViewNumberLayoutParams);

                        EditText editTextNUmber = new EditText(this);
                        editTextNUmber.setHint(String.format("Type %s here.",fieldName));
                        editTextNUmber.setHintTextColor(ContextCompat.getColor(this,R.color.hint));
                        editTextNUmber.setImeOptions(EditorInfo.IME_ACTION_DONE);
                        editTextNUmber.setBackground(ContextCompat.getDrawable(this,android.R.color.transparent));
                        editTextNUmber.setLayoutParams(layoutParams);

                        //set Tags
                        if(required)
                        editTextNUmber.setTag("required");

                        container.addView(textViewNUmber);
                        container.addView(editTextNUmber);
                        break;

                    case "multiline":
                        //Textview for Label
                        TextView textViewMultiline = new TextView(this);
                        textViewMultiline.setText(String.format("%s :", fieldName));
                        textViewMultiline.setTextSize(16);
                        LinearLayout.LayoutParams textViewMultilineLayoutParams =  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        textViewMultilineLayoutParams.setMargins(50,25,50,10);
                        textViewMultiline.setLayoutParams(textViewMultilineLayoutParams);

                        EditText editTextMultiline = new EditText(this);
                        editTextMultiline.setHint(String.format("Type %s here.",fieldName));
                        editTextMultiline.setHintTextColor(ContextCompat.getColor(this,R.color.hint));
                        editTextMultiline.setGravity(Gravity.TOP);
                        editTextMultiline.setSingleLine(false);
                        editTextMultiline.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                        editTextMultiline.setBackground(ContextCompat.getDrawable(this,android.R.color.transparent));
                        LinearLayout.LayoutParams editTextMultilineParams =  new LinearLayout.LayoutParams(300, 300);
                        editTextMultilineParams.setMargins(50,10,50,10);
                        editTextMultiline.setLayoutParams(layoutParams);

                        //set Tags
                        if(required)
                        editTextMultiline.setTag("required");

                        container.addView(textViewMultiline);
                        container.addView(editTextMultiline);
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
                if(ensureValidated()){
                    Toast.makeText(this, "Report Added", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private boolean ensureValidated() {
        validateEditTexts();
        //validateSpinner();
        return valid;
    }

    private void validateEditTexts() {
        for (int i = 0; i < container.getChildCount(); i++) {
            if (container.getChildAt(i).getTag() != null && container.getChildAt(i).getTag().toString().contains("required")) {
                String viewClass = container.getChildAt(i).getClass().getName();
                if (viewClass.contains("EditText")) {
                    EditText et = (EditText) container.getChildAt(i);
                    if (et.getText().toString().trim().isEmpty()) {
                        et.setError("This field is required.");
                        valid =false;
                    }else {
                        et.setError(null);
                        valid = true;
                    }
                }
            }
        }
    }

    private void validateSpinner(){

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.enter2, R.anim.exit2);
    }
}
