package com.yasin.hubbler.EventBus;

import org.json.JSONObject;

/**
 * Created by im_yasinashraf started on 26/11/18.
 */
public class OnReportUpdateEvent {

    private String fieldName;
    private JSONObject value;

    public OnReportUpdateEvent(String fieldName, JSONObject value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    public String getFieldName() {
        return fieldName;
    }

    public JSONObject getValue() {
        return value;
    }
}
