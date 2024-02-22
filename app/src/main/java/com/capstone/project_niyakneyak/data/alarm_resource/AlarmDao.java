package com.capstone.project_niyakneyak.data.alarm_resource;

import com.capstone.project_niyakneyak.data.alarm_model.Alarm;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.util.List;

@Dao
public interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Alarm alarm);


    @Query("SELECT * FROM alarm_table ORDER BY alarmCode ASC")
    LiveData<List<Alarm>> getAlarms();

    @Update
    void update(Alarm alarm);

    @Query("DELETE FROM alarm_table")
    void deleteAll();

    @Query("DELETE FROM alarm_table WHERE alarmCode = :alarmCode")
    void delete(int alarmCode);
}
