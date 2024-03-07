package com.capstone.project_niyakneyak.data.history_resource;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.capstone.project_niyakneyak.data.history_model.HistoryData;

import java.util.List;

public class HistoryRepository {
    private final HistoryDao historyDao;
    private final LiveData<List<HistoryData>> historyLiveData;

    public HistoryRepository(Application application){
        HistoryDatabase db = HistoryDatabase.getInstance(application);
        historyDao = db.historyDao();
        historyLiveData = historyDao.getHistories();
    }

    public void insert(HistoryData data){
        HistoryDatabase.writeExecutor.execute(() -> historyDao.insert(data));
    }

    public LiveData<HistoryData> search(long ID){
        return historyDao.getHistoryByID(ID);
    }

    public LiveData<List<HistoryData>> getHistoryLiveData(){
        return historyDao.getHistories();
    }

    public void delete(long ID){
        HistoryDatabase.writeExecutor.execute(() -> historyDao.delete(ID));
    }

    public void deleteAll(){
        HistoryDatabase.writeExecutor.execute(historyDao::deleteAll);
    }
}
