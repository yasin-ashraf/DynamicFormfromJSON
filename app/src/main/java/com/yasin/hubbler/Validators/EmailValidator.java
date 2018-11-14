package com.yasin.hubbler.Validators;

import android.support.annotation.NonNull;

/**
 * Created by im_yasinashraf started on 13/11/18.
 */
public class EmailValidator {

    public boolean isValid(@NonNull CharSequence text) {
        String regex = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return (!text.equals("") && text.toString().matches(regex));
    }
}
