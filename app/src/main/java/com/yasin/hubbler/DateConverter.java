package com.yasin.hubbler;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by im_yasinashraf started on 24/5/18.
 */
public class DateConverter {

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

}
