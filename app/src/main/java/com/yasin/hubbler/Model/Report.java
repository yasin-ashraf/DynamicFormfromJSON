package com.yasin.hubbler.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.yasin.hubbler.Database.DateConverter;

import java.util.Date;

/**
 * Created by im_yasinashraf started on 3/11/18.
 */
@Entity
public class Report {

    @PrimaryKey(autoGenerate = true)
    private int Id;

    private String report;

    @TypeConverters(DateConverter.class)
    private Date addedTime;

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

    public Date getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(Date addedTime) {
        this.addedTime = addedTime;
    }
}
