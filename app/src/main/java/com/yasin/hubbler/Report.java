package com.yasin.hubbler;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by im_yasinashraf started on 3/11/18.
 */
@Entity
public class Report {

    @PrimaryKey(autoGenerate = true)
    private int Id;

    private String report;

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }
}
