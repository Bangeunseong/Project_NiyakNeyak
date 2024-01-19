package com.capstone.project_niyakneyak.data;

import android.util.Log;

import com.capstone.project_niyakneyak.data.model.MedsData;

import java.util.List;

public class MedsDataSource {
    private String username;
    public MedsDataSource(String username){
        Log.d("MedsDataSource", "Called");
        this.username = username;
    }


    public Result<List<MedsData>> read() {
        //TODO: Search for medication info. by using username
        return new Result.Error(new Exception("Database not connected"));
    }

    public Result<MedsData> write(MedsData data, int option){
        //TODO: Write added data
        return new Result.Error(new Exception("Database not connected"));
    }

    public Result<MedsData> search(MedsData target){
        return new Result.Error(new Exception("Database not connected"));
    }

    public Result<MedsData> modify(MedsData originData, MedsData changedData){
        return new Result.Error(new Exception("Database not connected"));
    }
}
