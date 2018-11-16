package com.yasin.hubbler.ViewGenerators;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.yasin.hubbler.R;

import java.util.ArrayList;

/**
 * Created by im_yasinashraf started on 16/11/18.
 */
public class SpinnerGenerator {

    private Context context;

    public SpinnerGenerator(Context context) {
        this.context = context;
    }

    public Spinner generateSpinner(ArrayList<String> options) {
        Spinner spinner = new Spinner(context);
        spinner.setMinimumWidth(700);
        spinner.setLayoutParams(getGeneralLayoutParams());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.spinner_item, options);
        spinner.setAdapter(adapter);
        return spinner;
    }

    private LinearLayout.LayoutParams getGeneralLayoutParams(){
        LinearLayout.LayoutParams generalLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        generalLayoutParams.setMargins(50, 25, 50, 10);
        return generalLayoutParams;
    }

}
