package com.capstone.project_niyakneyak.data;

import android.view.SurfaceControl;

import com.capstone.project_niyakneyak.data.model.MedsData;

import java.util.List;

public class MedsRepository {
    private static volatile MedsRepository instance;

    private MedsDataSource dataSource;

    private List<MedsData> medsData = null;

    private MedsRepository(MedsDataSource dataSource){this.dataSource = dataSource;}

    public static MedsRepository getInstance(MedsDataSource dataSource){
        if (instance == null) {
            instance = new MedsRepository(dataSource);
            if(instance.medsData == null){
                Result<List<MedsData>> result = dataSource.read();
                if(result instanceof Result.Success){
                    instance.medsData = ((Result.Success<List<MedsData>>) result).getData();
                }
            }
        }
        return instance;
    }

    //Search by userid
    public Result<MedsData> contains(MedsData data){
        return dataSource.search(data);
    }

    public Result<List<MedsData>> getDatas(){
        Result<List<MedsData>> result = dataSource.read();
        if(result instanceof Result.Success){
            return result;
        }
        else{
            if(medsData != null) return new Result.Success<>(medsData);
            else return new Result.Error(new Exception("Cannot get data from remote and repository"));
        }
    }

    public Result<MedsData> addData(MedsData data) {
        Result<MedsData> result = dataSource.write(data, 0);
        medsData.add(data);
        if(result instanceof Result.Success){
             return result;
        }
        else return new Result.Error(new Exception("Cannot connect to database but saved in local repository"));
    }

    public Result<MedsData> modifyData(MedsData originData, MedsData changedData){
        if(medsData.contains(originData)){
            MedsData data = medsData.get(medsData.indexOf(originData));
            data.setMeds_name(changedData.getMeds_name());
            data.setMeds_detail(changedData.getMeds_detail());

            Result<MedsData> result = dataSource.modify(originData, changedData);
            if(result instanceof Result.Success)
                return new Result.Success<>(changedData);
            else return new Result.Error(new Exception("Data modification failed!"));
        }
        return new Result.Error(new Exception("Data does not exists!"));
    }

    public Result<MedsData> deleteData(MedsData target){
        if(medsData.contains(target)){
            medsData.remove(target);
            dataSource.write(target, 1);
            return new Result.Success<>(target);
        }
        return new Result.Error(new Exception("Data does not exists!"));
    }
}
