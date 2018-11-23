package com.yasin.hubbler;

/**
 * Created by im_yasinashraf started on 19/11/18.
 */
public interface ReportObjectListener {

    void onUpdateReport(String report, String fieldName);
    void onCreateFinalReport(String key,String value);
}
