package com.capstone.project_niyakneyak.data.history_resource;

import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.capstone.project_niyakneyak.data.history_model.HistoryData;

public interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HistoryData historyData);
}
