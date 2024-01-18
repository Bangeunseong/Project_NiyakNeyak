package com.capstone.project_niyakneyak.data;

import android.util.Log;

import com.capstone.project_niyakneyak.data.model.MedsData;

import java.util.ArrayList;
import java.util.List;

public class MedsDataSource {
    private String username;
    private List<MedsData> medsDataList;
    public MedsDataSource(String username){medsDataList = new ArrayList<>(); this.username = username;}


    public Result<List<MedsData>> read() {
        //TODO: Search for medication info. by using username
        return new Result.Success<>(medsDataList);
    }

    public Result<MedsData> write(MedsData data, int option){
        boolean flag = false;
        //TODO: Write added data
        switch(option){
            case 0:
                medsDataList.add(data); flag = true;
                Log.d("Addition","Addition Success");
                break;
            case 1:
                //TODO: Delete data in Database
                flag = true;
                break;
        }

        if(flag) return new Result.Success<>(data);
        else return new Result.Error(new Exception("Write Operation Failed!"));
    }

    public Result<MedsData> search(MedsData target){
        if(medsDataList.contains(target)) return new Result.Success<>(target);
        return new Result.Error(new Exception("Search Operation Failed!"));
    }

    public Result<MedsData> modify(MedsData originData, MedsData changedData){
        if(medsDataList.contains(originData)){
            MedsData data = medsDataList.get(medsDataList.indexOf(originData));
            data.setMeds_name(changedData.getMeds_name());
            data.setMeds_detail(changedData.getMeds_detail());
            data.setMeds_date(changedData.getMeds_date());
            data.setMeds_time(changedData.getMeds_time());
        }
        return new Result.Error(new Exception("Modify Operation Failed!"));
    }
}
