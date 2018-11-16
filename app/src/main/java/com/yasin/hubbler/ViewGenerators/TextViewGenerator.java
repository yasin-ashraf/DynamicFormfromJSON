package com.yasin.hubbler.ViewGenerators;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yasin.hubbler.R;

/**
 * Created by im_yasinashraf started on 16/11/18.
 */
public class TextViewGenerator {

    private Context context;

    public TextViewGenerator(Context context) {
        this.context = context;
    }

    public TextView generateTextView(String fieldName) {
        TextView textView = new TextView(context);
        textView.setText(String.format("%s :", fieldName));
        textView.setTextSize(16);
        textView.setTextAppearance(context,R.style.regularStyle);
        textView.setLayoutParams(getGeneralLayoutParams());
        return textView;
    }

    private LinearLayout.LayoutParams getGeneralLayoutParams(){
        LinearLayout.LayoutParams generalLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        generalLayoutParams.setMargins(50, 25, 50, 10);
        return generalLayoutParams;
    }


}
