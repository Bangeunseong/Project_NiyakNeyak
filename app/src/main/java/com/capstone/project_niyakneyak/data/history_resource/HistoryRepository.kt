package com.capstone.project_niyakneyak.data.history_resource

import android.app.Application
import androidx.lifecycle.LiveData
import com.capstone.project_niyakneyak.data.history_model.HistoryData

class HistoryRepository(application: Application) {
    private val historyDao: HistoryDao?
    val historyLiveData: LiveData<List<HistoryData>>?

    init {
        val db: HistoryDatabase? = HistoryDatabase.getInstance(application)
        historyDao = db?.historyDao()
        historyLiveData = historyDao?.histories
    }

    fun insert(data: HistoryData?) {
        HistoryDatabase.writeExecutor.execute { historyDao!!.insert(data) }
    }

    fun search(id: Long): LiveData<HistoryData>? {
        return historyDao!!.getHistoryByID(id)
    }

    fun delete(id: Long) {
        HistoryDatabase.writeExecutor.execute { historyDao!!.delete(id) }
    }

    fun deleteAll() {
        HistoryDatabase.writeExecutor.execute { historyDao!!.deleteAll() }
    }
}