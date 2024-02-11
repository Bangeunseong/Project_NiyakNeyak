package com.capstone.project_niyakneyak.data.alarm_resource;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.capstone.project_niyakneyak.data.alarm_model.Alarm;

import java.util.List;

public class AlarmRepository {
    private final AlarmDao alarmDao;
    private final LiveData<List<Alarm>> alarmsLiveData;

    public AlarmRepository(Application application) {
        AlarmDatabase db = AlarmDatabase.getInstance(application);
        alarmDao = db.alarmDao();
        alarmsLiveData = alarmDao.getAlarms();
    }

    public void insert(Alarm alarm) {
        AlarmDatabase.writeExecutor.execute(() -> {
            alarmDao.insert(alarm);
        });
    }

    public void update(Alarm alarm) {
        AlarmDatabase.writeExecutor.execute(() -> {
            alarmDao.update(alarm);
        });
    }

    public LiveData<List<Alarm>> getAlarmsLiveData() {
        return alarmsLiveData;
    }

    public void delete(int alarmId){
        AlarmDatabase.writeExecutor.execute(() -> {
            alarmDao.delete(alarmId);
        });
    }
}
