package com.capstone.project_niyakneyak.data.patient_resource;


import com.capstone.project_niyakneyak.data.Result;
import com.capstone.project_niyakneyak.data.patient_model.MedsData;
import com.capstone.project_niyakneyak.data.patient_model.PatientData;

import java.io.Serializable;

import kotlinx.coroutines.sync.Semaphore;

public class PatientRepository implements Serializable {
    private static volatile PatientRepository instance;
    private final PatientDataSource dataSource;
    private PatientData patientData = null;

    private PatientRepository(PatientDataSource dataSource){
        this.dataSource = dataSource;
        if(patientData == null){
            Result<PatientData> result_read = dataSource.read_PatientData();
            if(result_read instanceof Result.Success)
                patientData = ((Result.Success<PatientData>) result_read).getData();
            else patientData = new PatientData();
        }
    }

    public static PatientRepository getInstance(PatientDataSource dataSource){
        if (instance == null) {
            instance = new PatientRepository(dataSource);
        }
        return instance;
    }


    //Getter for patient Info.
    public Result<PatientData> getPatientData(){
        Result<PatientData> result = dataSource.read_PatientData();
        if(result instanceof Result.Success){
            return result;
        }
        else if(result instanceof Result.Error){
            if(patientData != null) return new Result.Success<>(patientData);
        }
        return new Result.Error(new Exception("Cannot get data from remote and repository"));
    }


    //Useful Functions to search, add, modify, delete patient medication Info.
    public Result<MedsData> search_MedsData(MedsData data){
        Result<MedsData> result = dataSource.search_MedsData(data);
        if(result instanceof Result.Success){return result;}
        else{
            if(patientData.searchMedsData(data)) return new Result.Success<>(data);
            else return new Result.Error(new Exception("Data does not exists!"));
        }
    }
    public Result<MedsData> add_MedsData(MedsData data) {
        Result<MedsData> result = dataSource.write_MedsData(data, 0);
        patientData.addMedsData(data);
        if(result instanceof Result.Success){return result;}
        else {
            if(patientData != null) return new Result.Success<>(data);
            return new Result.Error(new Exception("Cannot connect to database but saved in local repository"));
        }
    }

    public Result<MedsData> modify_MedsData(MedsData originData, MedsData changedData){
        Result<MedsData> result = dataSource.write_MedsData(originData, changedData);
        boolean flag = patientData.modifyMedsData(originData, changedData);

        if(result instanceof Result.Success){return result;}
        else return flag ? new Result.Success<>(changedData) : new Result.Error(new Exception("Data does not exists!"));
    }

    public Result<MedsData> delete_MedsData(MedsData target){
        Result<MedsData> result = dataSource.write_MedsData(target, 1);
        boolean flag = patientData.deleteMedsData(target);

        if(result instanceof Result.Success) return result;
        else return flag ? new Result.Success<>(target) : new Result.Error(new Exception("Data does not exists!"));
    }
}
