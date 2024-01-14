package com.capstone.project_niyakneyak.data;

import android.util.Log;

import com.capstone.project_niyakneyak.data.model.MedsData;

import java.util.ArrayList;
import java.util.List;

public class MedsDataSource {
    private String username;
    private static final List<MedsData> medsDataList = new ArrayList<>();
    public MedsDataSource(String username){this.username = username;}

    public Result<List<MedsData>> read() {
        //TODO: Search for medication info. by using username
        return new Result.Success<>(medsDataList);
    }

    public Result<MedsData> write(MedsData data, int option){
        //TODO: Write added data
        switch(option){
            case 0:
                //medsDataList.add(data);
                Log.d("Addition","Addition Success");
                return new Result.Success<>(data);
            case 1:
                //TODO: Delete data in Database
                break;
        }
        return new Result.Error(new Exception("Write Operation Failed!"));
    }

    public Result<MedsData> search(MedsData target){
        //TODO: Search for target Data!
        return new Result.Error(new Exception("Search Operation Failed!"));
    }

    public Result<MedsData> modify(MedsData originData, MedsData changedData){
        //TODO: Modify data in Database
        return new Result.Error(new Exception("Modify Operation Failed!"));
    }
}
