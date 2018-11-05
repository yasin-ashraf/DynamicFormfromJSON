package com.yasin.hubbler;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.yasin.hubbler.Database.HubblerDatabase;

/**
 * Created by im_yasinashraf started on 5/11/18.
 */

public class DatabaseClient {

    private Context context;
    private static DatabaseClient mInstance;

    private HubblerDatabase hubblerDatabase;

    private DatabaseClient(Context context) {
        this.context = context;
        //creating the app database with Room database builder
        hubblerDatabase = Room.databaseBuilder(context,
                HubblerDatabase.class, "HubblerDatabase.db")
                .allowMainThreadQueries()
                .build();
    }

    static synchronized DatabaseClient getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseClient(context);
        }
        return mInstance;
    }

    HubblerDatabase getAppDatabase() {
        return hubblerDatabase;
    }
}
