package com.capstone.project_niyakneyak.data.history_resource;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.capstone.project_niyakneyak.data.history_model.HistoryData;

import java.util.List;

import kotlinx.coroutines.flow.Flow;

@Dao
public interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HistoryData historyData);

    @Update
    void update(HistoryData historyData);

    @Query("SELECT * FROM history_table ORDER BY ID ASC")
    LiveData<List<HistoryData>> getHistories();

    @Query("SELECT * FROM history_table WHERE ID = :ID")
    LiveData<HistoryData> getHistoryByID(long ID);

    @Query("DELETE FROM history_table WHERE ID = :ID")
    void delete(long ID);

    @Query("DELETE FROM history_table")
    void deleteAll();
}
