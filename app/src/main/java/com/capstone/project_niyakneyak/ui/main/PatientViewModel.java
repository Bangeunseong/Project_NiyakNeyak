package com.capstone.project_niyakneyak.ui.main;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.PatientRepository;
import com.capstone.project_niyakneyak.data.Result;
import com.capstone.project_niyakneyak.data.model.MedsData;
import com.capstone.project_niyakneyak.data.model.PatientData;
import com.capstone.project_niyakneyak.data.model.TimeData;

import java.io.Serializable;

public class PatientViewModel extends ViewModel implements Serializable {
    // Field
    // Modification result of time
    private MutableLiveData<ActionResult> timeResult = new MutableLiveData<>();
    // Action(Addition, Modification, Deletion) result of Medication Info.
    private MutableLiveData<ActionResult> actionResult = new MutableLiveData<>();
    private final PatientRepository patientRepository;

    // Constructor
    PatientViewModel(PatientRepository patientRepository){this.patientRepository = patientRepository;}

    // Result for actions(Addition, Modification, Deletion, Search)
    public LiveData<ActionResult> getActionResult(){return actionResult;}
    public LiveData<ActionResult> getTimeResult(){return timeResult;}

    public Result<PatientData> getPatientData(){
        Result<PatientData> result = patientRepository.getPatientData();
        if(result instanceof Result.Success){return result;}
        else return new Result.Error(new Exception("Data cannot be read"));
    }


    // Useful Functions for add, modify, delete, search in MedsData
    public void add_MedsData(MedsData data){
        Result<MedsData> result = patientRepository.add_MedsData(data);

        if(result instanceof Result.Success){
            Log.d("PatientViewModel", "Addition Success(Meds)");
            MedsData resultData = ((Result.Success<MedsData>)result).getData();
            actionResult.setValue(new ActionResult(new DataView("Added Meds: " + resultData.getMeds_name())));
        }
        else actionResult.setValue(new ActionResult(R.string.action_main_rcv_add_error));
    }

    public void modify_MedsData(MedsData target, MedsData changed){
        Result<MedsData> result = patientRepository.modify_MedsData(target, changed);
        if(result instanceof Result.Success){
            Log.d("PatientViewModel", "Modification Success(Meds)");
            MedsData resultData = ((Result.Success<MedsData>)result).getData();
            actionResult.setValue(new ActionResult(new DataView("Modified Meds: " + resultData.getMeds_name())));
        }
        else actionResult.setValue(new ActionResult(R.string.action_main_rcv_modify_error));
    }

    public void delete_MedsData(MedsData target){
        Result<MedsData> result = patientRepository.delete_MedsData(target);
        if(result instanceof Result.Success){
            Log.d("PatientViewModel", "Deletion Success(Meds)");
            MedsData resultData = ((Result.Success<MedsData>)result).getData();
            actionResult.setValue(new ActionResult(new DataView("Deleted Meds: " + resultData.getMeds_name())));
        }
        else{
            actionResult.setValue(new ActionResult(R.string.action_main_rcv_delete_error));
        }
    }

    public void search_MedsData(MedsData target){
        Result<MedsData> result = patientRepository.search_MedsData(target);
        if(result instanceof Result.Success){
            Log.d("PatientViewModel", "Search Success(Meds)");
            MedsData resultData = ((Result.Success<MedsData>)result).getData();
            actionResult.setValue(new ActionResult(new DataView("Searched Timer: " + resultData.getMeds_name())));
        }
        else actionResult.setValue(new ActionResult(R.string.action_main_rcv_search_error));
    }


    //Useful Functions for modify in TimeData
    public void modify_TimeData(TimeData target, TimeData changed){
        Result<TimeData> result = patientRepository.modify_TimeData(target, changed);
        if(result instanceof Result.Success){
            Log.d("PatientViewModel", "Modification Success(Time)");
            TimeData resultData = ((Result.Success<TimeData>)result).getData();
            timeResult.setValue(new ActionResult(new DataView("Modified Timer: " + resultData.getTime())));
        }
    }
}
