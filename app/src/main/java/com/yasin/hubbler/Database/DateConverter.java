package com.yasin.hubbler.Database;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by im_yasinashraf started on 24/5/18.
 * Converter class for Date Objects.(Room Database)
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
