package com.yasin.hubbler;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by im_yasinashraf started on 4/11/18.
 */
@Database(entities = {Report.class}, version = 1)
public abstract class HubblerDatabase extends RoomDatabase {

    public abstract ReportDao reportDao();

    private static HubblerDatabase INSTANCE;

    public static HubblerDatabase getInMemoryDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.inMemoryDatabaseBuilder(context.getApplicationContext(),
                            HubblerDatabase.class)
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
