package com.yasin.hubbler.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.yasin.hubbler.Model.Report;

/**
 * Created by im_yasinashraf started on 4/11/18.
 */
@Database(entities = {Report.class}, version = 1)
@TypeConverters({DateConverter.class})
public abstract class HubblerDatabase extends RoomDatabase {

    public abstract ReportDao reportDao();
}
