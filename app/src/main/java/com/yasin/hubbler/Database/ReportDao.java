package com.yasin.hubbler.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.yasin.hubbler.Model.Report;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by im_yasinashraf started on 4/11/18.
 */
@Dao
public interface ReportDao {

    @Insert(onConflict = REPLACE)
    void save(Report report);

    @Update
    void update(Report report);

    @Delete
    void delete(Report report);

    @Query("SELECT * FROM report")
    List<Report> load();
}
