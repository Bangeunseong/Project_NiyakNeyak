package com.capstone.project_niyakneyak.data;

import android.util.Log;

import com.capstone.project_niyakneyak.data.model.MedsData;
import com.capstone.project_niyakneyak.data.model.PatientData;
import com.capstone.project_niyakneyak.data.model.TimeData;

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

    public Result<TimeData> search_TimeData(TimeData target){
        return new Result.Error(new Exception("Database not connected"));
    }
    public Result<TimeData> write_TimeData(TimeData data, int option){
        //TODO: Write added data or delete target data
        return new Result.Error(new Exception("Database not connected"));
    }
    public Result<TimeData> write_TimeData(TimeData originData, TimeData changedData){
        //TODO: Modify added data
        return new Result.Error(new Exception("Database not connected"));
    }
}
