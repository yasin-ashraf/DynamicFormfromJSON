package com.yasin.hubbler;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by im_yasinashraf started on 4/11/18.
 */
@Dao
public interface ReportDao {

    @Insert(onConflict = REPLACE)
    void save(Report report);

    @Query("SELECT * FROM report")
    List<Report> load();
}
