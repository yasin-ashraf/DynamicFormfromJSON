package com.yasin.hubbler;

import android.app.Activity;
import android.app.Application;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by im_yasinashraf started on 4/11/18.
 */
public class Hubbler extends Application {

    private Executor executor;

    @Override
    public void onCreate() {
        super.onCreate();
        executor = Executors.newCachedThreadPool();
    }

    public static Hubbler getApp(Activity activity) {
        return (Hubbler) activity.getApplication();
    }

    public Executor getExecutor() {
        return executor;
    }
}
