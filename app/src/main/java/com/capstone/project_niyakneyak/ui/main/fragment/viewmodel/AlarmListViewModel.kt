package com.capstone.project_niyakneyak.ui.main.fragment.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.capstone.project_niyakneyak.data.Result
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.data.alarm_resource.AlarmRepository
import com.capstone.project_niyakneyak.data.patient_model.PatientData
import com.capstone.project_niyakneyak.data.patient_resource.PatientDataSource
import com.capstone.project_niyakneyak.data.patient_resource.PatientRepository

class AlarmListViewModel(application: Application) : AndroidViewModel(application) {
    private val alarmRepository: AlarmRepository
    private val patientRepository: PatientRepository
    private val alarmsLiveData: LiveData<List<Alarm>>

    init {
        alarmRepository = AlarmRepository(application)
        patientRepository = PatientRepository.getInstance(PatientDataSource())!!
        alarmsLiveData = alarmRepository.alarmsLiveData!!
    }

    fun update(alarm: Alarm?) {
        alarmRepository.update(alarm)
    }

    fun delete(alarmId: Int) {
        alarmRepository.delete(alarmId)
    }

    fun getAlarmsLiveData(): LiveData<List<Alarm>> {
        return alarmsLiveData
    }

    fun getPatientData(): PatientData? {
        val result = patientRepository.getPatientData()
        return if(result is Result.Success<*>) (result as Result.Success<PatientData>).data else null
    }
}