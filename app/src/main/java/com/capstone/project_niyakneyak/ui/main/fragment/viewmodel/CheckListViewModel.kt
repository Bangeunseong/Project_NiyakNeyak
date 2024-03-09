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
import com.capstone.project_niyakneyak.data.patient_resource.PatientDataSource
import com.capstone.project_niyakneyak.data.patient_resource.PatientRepository
import com.capstone.project_niyakneyak.ui.main.etc.ActionResult

class CheckListViewModel(application: Application) : AndroidViewModel(application) {
    private val alarmRepository: AlarmRepository
    private val patientRepository: PatientRepository

    init {
        patientRepository = PatientRepository.getInstance(PatientDataSource())!!
        alarmRepository = AlarmRepository(application)
    }

    fun getAlarmsLiveData(): LiveData<List<Alarm>>? {
        return alarmRepository.alarmsLiveData
    }

    fun getLiveActionResult(): MutableLiveData<ActionResult> {
        return patientRepository.actionResult
    }

    fun getMedsDataList(): List<MedsData>? {
        val result = patientRepository.getPatientData()
        return if (result is Result.Success<*>) (result as Result.Success<PatientData>).data.medsData else null
    }
}