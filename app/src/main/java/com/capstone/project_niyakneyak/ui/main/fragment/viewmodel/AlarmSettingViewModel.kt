package com.capstone.project_niyakneyak.ui.main.fragment.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.data.alarm_resource.AlarmRepository

class AlarmSettingViewModel(application: Application) : AndroidViewModel(application) {
    private val alarmRepository: AlarmRepository

    init {
        alarmRepository = AlarmRepository(application)
    }

    fun insert(alarm: Alarm) {
        alarmRepository.insert(alarm)
    }

    fun update(alarm: Alarm) {
        alarmRepository.update(alarm)
    }

    fun getAlarmsLiveData(): LiveData<List<Alarm>>? {
        return alarmRepository.alarmsLiveData
    }
}