package com.capstone.project_niyakneyak.ui.alarm.service

import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.data.alarm_resource.AlarmRepository

class RescheduleAlarmService : LifecycleService() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val alarmRepository = AlarmRepository(application)
        alarmRepository.alarmsLiveData!!.observe(this) { alarms: List<Alarm?>? ->
            for (a in alarms!!) {
                if (a!!.isStarted) {
                    a.scheduleAlarm(applicationContext)
                }
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }
}