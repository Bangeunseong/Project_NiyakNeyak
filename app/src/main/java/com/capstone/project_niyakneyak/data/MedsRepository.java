package com.capstone.project_niyakneyak.data;

import android.util.Log;
import android.view.SurfaceControl;

import com.capstone.project_niyakneyak.data.model.MedsData;

import java.util.ArrayList;
import java.util.List;

public class MedsRepository {
    private static volatile MedsRepository instance;

    private MedsDataSource dataSource;

    private List<MedsData> medsData = null;

    private MedsRepository(MedsDataSource dataSource){this.dataSource = dataSource;}

    public static MedsRepository getInstance(MedsDataSource dataSource){
        if (instance == null) {
            instance = new MedsRepository(dataSource);
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
            if(medsData == null) medsData = ((Result.Success<List<MedsData>>)result).getData();
            return result;
        }
        else{
            if(medsData != null) return new Result.Success<>(medsData);
            else return new Result.Error(new Exception("Cannot get data from remote and repository"));
        }
    }

    public Result<MedsData> addData(MedsData data) {
        Result<MedsData> result = dataSource.write(data, 0);
        if(result instanceof Result.Success){
            if(medsData == null){
                Result<List<MedsData>> result_read = dataSource.read();
                if(result_read instanceof Result.Success)
                    medsData = ((Result.Success<List<MedsData>>)result_read).getData();
                else medsData = new ArrayList<>();
            }
            medsData.stream().forEach(d-> Log.d("repository",d.getMeds_name()));
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
            if(result instanceof Result.Success) {
                if(medsData == null){
                    Result<List<MedsData>> result_read = dataSource.read();
                    if(result_read instanceof Result.Success)
                        medsData = ((Result.Success<List<MedsData>>)result_read).getData();
                    else medsData = new ArrayList<>();
                }
                return new Result.Success<>(changedData);
            }
            else return new Result.Error(new Exception("Data modification failed!"));
        }
        return new Result.Error(new Exception("Data does not exists!"));
    }

    public Result<MedsData> deleteData(MedsData target){
        Result<MedsData> result = dataSource.search(target);
        if(result instanceof Result.Success){
            dataSource.write(target, 1);
            if(medsData == null){
                Result<List<MedsData>> result_read = dataSource.read();
                if(result_read instanceof Result.Success)
                    medsData = ((Result.Success<List<MedsData>>)result_read).getData();
                else medsData = new ArrayList<>();
            }
            return new Result.Success<>(target);
        }
        return new Result.Error(new Exception("Data does not exists!"));
    }
}
