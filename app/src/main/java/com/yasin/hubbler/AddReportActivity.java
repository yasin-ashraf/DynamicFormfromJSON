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
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by im_yasinashraf started on 1/11/18.
 */
public class AddReportActivity extends AppCompatActivity {

    private String jsonData;
    private LinearLayout container;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_report);
        container = findViewById(R.id.container);

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
                String required = null;
                if(viewObject.has("required")){
                    required = viewObject.getString("required");
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
//                        if(required != null) textInputLayout.setError("this field is required!");
                        TextInputEditText textInputEditText = new TextInputEditText(this);
                        textInputEditText.setHint(fieldName);
                        textInputEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                        textInputLayout.setBackground(ContextCompat.getDrawable(this,R.drawable.edge));
                        LinearLayout.LayoutParams textInputLayoutParams =  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        textInputLayoutParams.setMargins(0,10,0,10);
                        textInputEditText.setLayoutParams(textInputLayoutParams);
                        textInputLayout.addView(textInputEditText);

                        // add layout params
                        textInputLayout.setLayoutParams(layoutParams);

                        container.addView(textInputLayout);
                        break;

                    case "number":
                        break;

                    case "multiline":
                        TextInputLayout multiLineTextInputLayout = new TextInputLayout(this);
                        TextInputEditText multiLineEditText = new TextInputEditText(this);
                        multiLineEditText.setHint(fieldName);
                        multiLineEditText.setSingleLine(false);
                        multiLineEditText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                        multiLineEditText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);

                        LinearLayout.LayoutParams multilineLayoutParams =  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 350);
                        multilineLayoutParams.setMargins(0,10,0,10);
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
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.enter2, R.anim.exit2);
    }
}
