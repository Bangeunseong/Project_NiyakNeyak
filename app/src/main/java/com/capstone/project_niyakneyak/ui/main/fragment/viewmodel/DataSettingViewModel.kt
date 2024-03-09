package com.capstone.project_niyakneyak.ui.main.fragment.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.data.alarm_resource.AlarmRepository

class DataSettingViewModel(application: Application) : AndroidViewModel(application) {
    private val alarmRepository: AlarmRepository
    private val alarmsLiveData: LiveData<List<Alarm>>

    init {
        alarmRepository = AlarmRepository(application)
        alarmsLiveData = alarmRepository.alarmsLiveData!!
    }

    fun getAlarmsLiveData(): LiveData<List<Alarm>>{
        return alarmsLiveData
    }
}