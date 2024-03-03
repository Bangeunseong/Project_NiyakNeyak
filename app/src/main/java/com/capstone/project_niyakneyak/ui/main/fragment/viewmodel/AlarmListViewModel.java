package com.capstone.project_niyakneyak.ui.main.fragment.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.capstone.project_niyakneyak.data.Result;
import com.capstone.project_niyakneyak.data.alarm_resource.AlarmRepository;
import com.capstone.project_niyakneyak.data.alarm_model.Alarm;
import com.capstone.project_niyakneyak.data.patient_model.PatientData;
import com.capstone.project_niyakneyak.data.patient_resource.PatientDataSource;
import com.capstone.project_niyakneyak.data.patient_resource.PatientRepository;

import java.util.ArrayList;
import java.util.List;

public class AlarmListViewModel extends AndroidViewModel {
    private final AlarmRepository alarmRepository;
    private final PatientRepository patientRepository;
    private final LiveData<List<Alarm>> alarmList_LiveData;

    public AlarmListViewModel(@NonNull Application application) {
        super(application);
        alarmRepository = new AlarmRepository(application);
        patientRepository = PatientRepository.getInstance(new PatientDataSource());
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

    public PatientData getPatientData(){
        Result<PatientData> result = patientRepository.getPatientData();
        if(result instanceof Result.Success)
            return ((Result.Success<PatientData>) result).getData();
        return null;
    }
}
