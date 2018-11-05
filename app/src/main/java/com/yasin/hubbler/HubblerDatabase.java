package com.yasin.hubbler;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by im_yasinashraf started on 4/11/18.
 */
@Database(entities = {Report.class}, version = 1)
public abstract class HubblerDatabase extends RoomDatabase {

    public abstract ReportDao reportDao();
}
