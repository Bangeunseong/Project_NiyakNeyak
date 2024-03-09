package com.capstone.project_niyakneyak.data.alarm_resource

import android.app.Application
import androidx.lifecycle.LiveData
import com.capstone.project_niyakneyak.data.alarm_model.Alarm

class AlarmRepository(application: Application) {
    private val alarmDao: AlarmDao?
    @JvmField
    val alarmsLiveData: LiveData<List<Alarm>>?

    init {
        val db: AlarmDatabase? = AlarmDatabase.getInstance(application)
        alarmDao = db?.alarmDao()
        alarmsLiveData = alarmDao?.alarms
    }

    fun insert(alarm: Alarm?) {
        AlarmDatabase.writeExecutor.execute { alarmDao!!.insert(alarm) }
    }

    fun update(alarm: Alarm?) {
        AlarmDatabase.writeExecutor.execute { alarmDao!!.update(alarm) }
    }

    fun delete(alarmId: Int) {
        AlarmDatabase.writeExecutor.execute { alarmDao!!.delete(alarmId) }
    }
}