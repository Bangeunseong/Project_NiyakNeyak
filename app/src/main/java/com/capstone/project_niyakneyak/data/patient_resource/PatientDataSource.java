package com.capstone.project_niyakneyak.data.patient_resource;

import android.util.Log;

import com.capstone.project_niyakneyak.data.Result;
import com.capstone.project_niyakneyak.data.patient_model.MedsData;
import com.capstone.project_niyakneyak.data.patient_model.PatientData;

public class PatientDataSource {
    public PatientDataSource(){
        Log.d("PatientDataSource", "Called");
    }

    public Result<PatientData> read_PatientData() {
        //TODO: Search for patient info. by using userid
        return new Result.Error(new Exception("Database not connected"));
    }

    public Result<MedsData> search_MedsData(MedsData target){
        //TODO: Search for medication info.
        return new Result.Error(new Exception("Database not connected"));
    }
    public Result<MedsData> write_MedsData(MedsData data, int option){
        //TODO: Write added data or delete target data
        return new Result.Error(new Exception("Database not connected"));
    }
    public Result<MedsData> write_MedsData(MedsData originData, MedsData changedData){
        //TODO: Modify added data
        return new Result.Error(new Exception("Database not connected"));
    }
}
