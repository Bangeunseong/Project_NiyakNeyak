package com.capstone.project_niyakneyak.ui.main.fragment.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.capstone.project_niyakneyak.data.alarm_model.Alarm;
import com.capstone.project_niyakneyak.data.alarm_resource.AlarmRepository;

import java.util.List;

public class AlarmSettingViewModel extends AndroidViewModel {
    private final AlarmRepository alarmRepository;

    public AlarmSettingViewModel(@NonNull Application application){
        super(application);
        alarmRepository = new AlarmRepository(application);
    }

    public void insert(Alarm alarm) {
        alarmRepository.insert(alarm);
    }
    public void update(Alarm alarm) {
        alarmRepository.update(alarm);
    }
    public LiveData<List<Alarm>> getAlarmsLiveData(){ return alarmRepository.getAlarmsLiveData(); }
}
