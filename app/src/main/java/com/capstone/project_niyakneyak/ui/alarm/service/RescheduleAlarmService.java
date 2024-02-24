package com.capstone.project_niyakneyak.ui.alarm.service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;

import android.content.Intent;
import android.os.IBinder;

import com.capstone.project_niyakneyak.data.alarm_resource.AlarmRepository;
import com.capstone.project_niyakneyak.data.alarm_model.Alarm;

public class RescheduleAlarmService extends LifecycleService {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        AlarmRepository alarmRepository = new AlarmRepository(getApplication());
        alarmRepository.getAlarmsLiveData().observe(this, alarms -> {
            for (Alarm a : alarms) {
                if (a.isStarted()) {
                    a.scheduleAlarm(getApplicationContext());
                }
            }
        });

        return START_STICKY;
    }

    @Override
    public IBinder onBind(@NonNull Intent intent) {
        super.onBind(intent);
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}