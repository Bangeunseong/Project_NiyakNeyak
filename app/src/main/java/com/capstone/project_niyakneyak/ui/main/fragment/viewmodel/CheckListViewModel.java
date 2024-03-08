package com.capstone.project_niyakneyak.ui.main.fragment.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.capstone.project_niyakneyak.data.Result;
import com.capstone.project_niyakneyak.data.alarm_model.Alarm;
import com.capstone.project_niyakneyak.data.alarm_resource.AlarmRepository;
import com.capstone.project_niyakneyak.data.patient_model.MedsData;
import com.capstone.project_niyakneyak.data.patient_model.PatientData;
import com.capstone.project_niyakneyak.data.patient_resource.PatientDataSource;
import com.capstone.project_niyakneyak.data.patient_resource.PatientRepository;
import com.capstone.project_niyakneyak.ui.main.etc.ActionResult;

import java.util.List;

public class CheckListViewModel extends AndroidViewModel {
    private final AlarmRepository alarmRepository;
    private final PatientRepository patientRepository;
    public CheckListViewModel(@NonNull Application application) {
        super(application);
        patientRepository = PatientRepository.getInstance(new PatientDataSource());
        alarmRepository = new AlarmRepository(application);
    }

    public LiveData<List<Alarm>> getAlarmsLiveData(){ return alarmRepository.getAlarmsLiveData(); }
    public List<MedsData> getMedsDataList(){
        Result<PatientData> result = patientRepository.getPatientData();
        if(result instanceof Result.Success)
            return ((Result.Success<PatientData>) result).getData().getMedsData();
        return null;
    }
    public MutableLiveData<ActionResult> getLiveActionResult(){
        return patientRepository.getActionResult();
    }
}
