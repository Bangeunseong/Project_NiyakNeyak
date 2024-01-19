package com.capstone.project_niyakneyak.ui.main;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.MedsRepository;
import com.capstone.project_niyakneyak.data.Result;
import com.capstone.project_niyakneyak.data.model.MedsData;

import java.util.List;

public class MedsViewModel extends ViewModel {
    private MutableLiveData<ActionResult> actionResult = new MutableLiveData<>();
    private final MedsRepository medsRepository;

    MedsViewModel(MedsRepository medsRepository){this.medsRepository = medsRepository;}

    LiveData<ActionResult> getActionResult(){return actionResult;}

    public Result<List<MedsData>> getDatas(){
        Result<List<MedsData>> result = medsRepository.getDatas();
        if(result instanceof Result.Success){
            return result;
        }
        else return new Result.Error(new Exception("Data cannot be read"));
    }

    public void addData(MedsData data){
        Result<MedsData> result = medsRepository.addData(data);

        if(result instanceof Result.Success){
            Log.d("MedsViewModel", "Addition Success");
            MedsData resultData = ((Result.Success<MedsData>)result).getData();
            actionResult.setValue(new ActionResult(new MedsDataView(resultData.getMeds_name())));
        }
        else{
            actionResult.setValue(new ActionResult(R.string.action_main_rcv_add_error));
        }
    }

    public void modifyData(MedsData target, MedsData changed){
        Result<MedsData> result = medsRepository.modifyData(target, changed);
        if(result instanceof Result.Success){
            MedsData resultData = ((Result.Success<MedsData>)result).getData();
            actionResult.setValue(new ActionResult(new MedsDataView(resultData.getMeds_name())));
        }
        else{
            Log.d("MedsViewModel", result.toString());
            actionResult.setValue(new ActionResult(R.string.action_main_rcv_modify_error));
        }
    }

    public void deleteData(MedsData target){
        Result<MedsData> result = medsRepository.deleteData(target);
        if(result instanceof Result.Success){
            MedsData resultData = ((Result.Success<MedsData>)result).getData();
            actionResult.setValue(new ActionResult(new MedsDataView(resultData.getMeds_name())));
        }
        else{
            actionResult.setValue(new ActionResult(R.string.action_main_rcv_delete_error));
        }
    }

    public void contains(MedsData target){
        Result<MedsData> result = medsRepository.contains(target);
        if(result instanceof Result.Success){
            MedsData resultData = ((Result.Success<MedsData>)result).getData();
            actionResult.setValue(new ActionResult(new MedsDataView(resultData.getMeds_name())));
        }
        else{
            actionResult.setValue(new ActionResult(R.string.action_main_rcv_search_error));
        }
    }
}
