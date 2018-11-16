package com.yasin.hubbler.ViewGenerators;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.yasin.hubbler.R;

/**
 * Created by im_yasinashraf started on 16/11/18.
 */
public class EditTextGenerator {

    private Context context;

    public EditTextGenerator(Context context) {
        this.context = context;
    }

    public EditText generateEditText(String type, String fieldName, Boolean required, int min, int max){
        EditText editText = new EditText(this.context);
        editText.setHint(String.format(context.getString(R.string.label_type_here), fieldName));
        editText.setHintTextColor(ContextCompat.getColor(this.context, R.color.hint));
        editText.setBackground(ContextCompat.getDrawable(this.context, android.R.color.transparent));
        if(type.equals(context.getString(R.string.label_multiline))){
            editText.setGravity(Gravity.TOP);
            editText.setSingleLine(false);
            editText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
            editText.setMinHeight(300);
        }else {
            editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            editText.setSingleLine(true);
        }
        if(type.equals(context.getString(R.string.label_email))){
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }else if(type.equals(context.getString(R.string.label_number))){
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        editText.setLayoutParams(getEditFieldLayoutParams());
        if (required) editText.setTag(context.getString(R.string.label_required) + ";" + fieldName);
        else editText.setTag(fieldName);//set required tag
        return editText;
    }

    private LinearLayout.LayoutParams getEditFieldLayoutParams(){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(50, 10, 50, 10);
        return layoutParams;
    }


}
