package com.yasin.hubbler.ViewGenerators;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yasin.hubbler.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by im_yasinashraf started on 18/11/18.
 */
public class CompositeBoxViewGenerator {

    private Context context;

    public CompositeBoxViewGenerator(Context context) {
        this.context = context;
    }

    public LinearLayout createBoxView(String fieldName){
        LinearLayout li = new LinearLayout(context);
        li.setLayoutParams(getGeneralLayoutParams());
        TextView textView = new TextView(context);
        textView.setTextSize(16);
        textView.setText(String.format(context.getString(R.string.label_type_here),fieldName));
        textView.setTextColor(ContextCompat.getColor(context,R.color.hint));
        textView.setLayoutParams(getTextViewLayoutParams());
        li.addView(textView);
        return li;
    }

    public LinearLayout createBoxViewWithTypedValues(JSONObject fieldValues){
        String text = "";
        for(int i = 0; i<fieldValues.names().length(); i++){
            try {
               text = text.concat(fieldValues.getString(fieldValues.names().getString(i))).concat(" ").concat(",");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        LinearLayout li = new LinearLayout(context);
        li.setLayoutParams(getGeneralLayoutParamsForValues());
        TextView textView = new TextView(context);
        textView.setTextSize(18);
        textView.setTextColor(ContextCompat.getColor(context,android.R.color.black));
        textView.setText(removeLastChar(text));
        textView.setLayoutParams(getTextViewLayoutParams());
        li.addView(textView);
        return li;
    }


    private LinearLayout.LayoutParams getGeneralLayoutParams(){
        LinearLayout.LayoutParams generalLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
        generalLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        generalLayoutParams.setMargins(50, 10, 50, 10);
        return generalLayoutParams;
    }

    private LinearLayout.LayoutParams getGeneralLayoutParamsForValues(){
        LinearLayout.LayoutParams generalLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        generalLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        generalLayoutParams.setMargins(50, 10, 50, 10);
        return generalLayoutParams;
    }


    private LinearLayout.LayoutParams getTextViewLayoutParams(){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 25, 0, 0);
        return layoutParams;
    }

    private String removeLastChar(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        return s.substring(0, s.length()-1);
    }

}
