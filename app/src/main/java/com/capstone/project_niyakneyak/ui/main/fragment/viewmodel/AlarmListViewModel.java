package com.capstone.project_niyakneyak.ui.main.fragment.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.capstone.project_niyakneyak.data.alarm_resource.AlarmRepository;
import com.capstone.project_niyakneyak.data.alarm_model.Alarm;

import java.io.Serializable;
import java.util.List;

public class AlarmListViewModel extends AndroidViewModel implements Serializable {
    private final AlarmRepository alarmRepository;
    private final LiveData<List<Alarm>> alarmList_LiveData;

    public AlarmListViewModel(@NonNull Application application) {
        super(application);
        alarmRepository = new AlarmRepository(application);
        alarmList_LiveData = alarmRepository.getAlarmsLiveData();
    }

    public void update(Alarm alarm) {
        alarmRepository.update(alarm);
    }

    public LiveData<List<Alarm>> getAlarmsLiveData() {
        return alarmList_LiveData;
    }

    public void delete(int alarmId){
        alarmRepository.delete(alarmId);
    }
}
