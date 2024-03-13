package com.capstone.project_niyakneyak.main.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.data.alarm_resource.AlarmRepository

class CheckListViewModel(application: Application) : AndroidViewModel(application) {
    private val alarmRepository: AlarmRepository

    init {
        alarmRepository = AlarmRepository(application)
    }

    fun getAlarmsLiveData(): LiveData<List<Alarm>>? {
        return alarmRepository.alarmsLiveData
    }
}