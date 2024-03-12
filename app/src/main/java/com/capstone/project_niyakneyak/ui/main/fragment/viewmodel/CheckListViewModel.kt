package com.capstone.project_niyakneyak.ui.main.fragment.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.capstone.project_niyakneyak.data.Result
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.data.alarm_resource.AlarmRepository
import com.capstone.project_niyakneyak.data.patient_model.MedsData
import com.capstone.project_niyakneyak.data.patient_model.PatientData
import com.capstone.project_niyakneyak.ui.main.etc.ActionResult

class CheckListViewModel(application: Application) : AndroidViewModel(application) {
    private val alarmRepository: AlarmRepository

    init {
        alarmRepository = AlarmRepository(application)
    }

    fun getAlarmsLiveData(): LiveData<List<Alarm>>? {
        return alarmRepository.alarmsLiveData
    }
}