package com.capstone.project_niyakneyak.ui.main.fragment.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.capstone.project_niyakneyak.data.alarm_model.Alarm;
import com.capstone.project_niyakneyak.data.alarm_resource.AlarmRepository;

import java.util.List;

public class DataSettingViewModel extends AndroidViewModel {
    private AlarmRepository alarmRepository;
    private final LiveData<List<Alarm>> alarmList_LiveData;
    public DataSettingViewModel(@NonNull Application application) {
        super(application);
        alarmRepository = new AlarmRepository(application);
        alarmList_LiveData = alarmRepository.getAlarmsLiveData();
    }
    public LiveData<List<Alarm>> getAlarmsLiveData() {
        return alarmList_LiveData;
    }
}
