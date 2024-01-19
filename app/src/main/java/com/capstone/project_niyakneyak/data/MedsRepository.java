package com.capstone.project_niyakneyak.data;


import com.capstone.project_niyakneyak.data.model.MedsData;

import java.util.ArrayList;
import java.util.List;

public class MedsRepository {
    private static volatile MedsRepository instance;

    private MedsDataSource dataSource;
    private List<MedsData> medsData = null;

    private MedsRepository(MedsDataSource dataSource){
        this.dataSource = dataSource;
        if(medsData == null){
            Result<List<MedsData>> result_read = dataSource.read();
            if(result_read instanceof Result.Success)
                medsData = ((Result.Success<List<MedsData>>) result_read).getData();
            else medsData = new ArrayList<>();
        }
    }

    public static MedsRepository getInstance(MedsDataSource dataSource){
        if (instance == null) {
            instance = new MedsRepository(dataSource);
        }
        return instance;
    }

    //Search by userid
    public Result<MedsData> contains(MedsData data){
        Result<MedsData> result = dataSource.search(data);
        if(result instanceof Result.Success){return result;}
        else{
            if(medsData.contains(data)) return new Result.Success<>(data);
            else return new Result.Error(new Exception("Data does not exists!"));
        }
    }

    public Result<List<MedsData>> getDatas(){
        Result<List<MedsData>> result = dataSource.read();
        if(result instanceof Result.Success){
            return result;
        }
        else if(result instanceof Result.Error){
            if(medsData != null) return new Result.Success<>(medsData);
        }
        return new Result.Error(new Exception("Cannot get data from remote and repository"));
    }

    public Result<MedsData> addData(MedsData data) {
        Result<MedsData> result = dataSource.write(data, 0);
        medsData.add(data);
        if(result instanceof Result.Success){return result;}
        else {
            if(medsData != null) return new Result.Success<>(data);
            return new Result.Error(new Exception("Cannot connect to database but saved in local repository"));
        }
    }

    public Result<MedsData> modifyData(MedsData originData, MedsData changedData){
        Result<MedsData> result_search = dataSource.search(originData);
        if(result_search instanceof Result.Success){
            Result<MedsData> result_modify = dataSource.modify(originData, changedData);
            if(medsData.contains(originData)){
                MedsData data = medsData.get(medsData.indexOf(originData));
                data.setID(changedData.getID()); data.setMeds_name(changedData.getMeds_name()); data.setMeds_date(changedData.getMeds_date());
                data.setMeds_detail(changedData.getMeds_detail()); data.setMeds_time(changedData.getMeds_time());
            }
            if(result_modify instanceof Result.Success){return result_modify;}
            else {return new Result.Error(new Exception("Modification Failed"));}
        }
        else{
            if(medsData.contains(originData)){
                MedsData data = medsData.get(medsData.indexOf(originData));
                data.setID(changedData.getID()); data.setMeds_name(changedData.getMeds_name()); data.setMeds_date(changedData.getMeds_date());
                data.setMeds_detail(changedData.getMeds_detail()); data.setMeds_time(changedData.getMeds_time());
                return new Result.Success<>(changedData);
            }
        }
        return new Result.Error(new Exception("Data does not exists!"));
    }

    public Result<MedsData> deleteData(MedsData target){
        Result<MedsData> result = dataSource.write(target, 1);
        boolean flag = medsData.remove(target);

        if(result instanceof Result.Success) return result;
        else{if(flag) return new Result.Success<>(target);}

        return new Result.Error(new Exception("Data does not exists"));
    }
}
