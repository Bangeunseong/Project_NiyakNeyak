package com.capstone.project_niyakneyak.data.history_resource

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.capstone.project_niyakneyak.data.history_model.HistoryData

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(historyData: HistoryData?)

    @Update
    fun update(historyData: HistoryData?)

    @get:Query("SELECT * FROM history_table")
    val histories: LiveData<List<HistoryData>>?

    @Query("SELECT * FROM history_table WHERE id = :id")
    fun getHistoryByID(id: Long): LiveData<HistoryData>?

    @Query("DELETE FROM history_table WHERE id = :ID")
    fun delete(ID: Long)

    @Query("DELETE FROM history_table")
    fun deleteAll()
}