package com.yasin.hubbler.Validators;

import android.support.annotation.NonNull;


/**
 * Created by im_yasinashraf started on 13/11/18.
 */
public class NumberValidator {

    private boolean valid = false;

    public boolean isValid(@NonNull CharSequence text,int min,int max) {
        final int a = !text.toString().equals("") ? Integer.parseInt(text.toString()) : 0;
        if (min < a) {
            valid = a < max;
        } else {
            valid = false;
        }
        return valid;
    }
}
