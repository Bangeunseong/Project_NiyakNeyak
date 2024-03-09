package com.capstone.project_niyakneyak.data.alarm_resource

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.capstone.project_niyakneyak.data.alarm_model.Alarm

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(alarm: Alarm?)

    @get:Query("SELECT * FROM alarm_table")
    val alarms: LiveData<List<Alarm>>

    @Update
    fun update(alarm: Alarm?)

    @Query("DELETE FROM alarm_table WHERE alarmCode = :alarmCode")
    fun delete(alarmCode: Int)

    @Query("DELETE FROM alarm_table")
    fun deleteAll()
}