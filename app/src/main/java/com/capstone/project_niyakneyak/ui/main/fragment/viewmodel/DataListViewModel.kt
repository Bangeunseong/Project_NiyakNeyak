package com.capstone.project_niyakneyak.ui.main.fragment.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.Result
import com.capstone.project_niyakneyak.data.patient_model.MedsData
import com.capstone.project_niyakneyak.data.patient_model.PatientData
import com.capstone.project_niyakneyak.data.patient_resource.PatientDataSource
import com.capstone.project_niyakneyak.data.patient_resource.PatientRepository
import com.capstone.project_niyakneyak.ui.main.etc.ActionResult
import com.capstone.project_niyakneyak.ui.main.etc.DataView

class DataListViewModel : ViewModel() {
    // Field
    // Action(Addition, Modification, Deletion) result of Medication Info.
    private val actionResult = MutableLiveData<ActionResult?>()
    private val patientRepository: PatientRepository? = PatientRepository.getInstance(PatientDataSource())

    // Result for actions(Addition, Modification, Deletion, Search)
    val patientData: Result<out Any?>
        get() {
            val result = patientRepository?.getPatientData()
            return if (result is Result.Success<*>) {
                result as Result<PatientData>
            } else Result.Error(Exception("Data cannot be read"))
        }

    fun getActionResult(): LiveData<ActionResult?> {
        return actionResult
    }

    // Useful Functions for add, modify, delete, search in MedsData
    fun addMedsdata(data: MedsData) {
        val result = patientRepository?.addMedsData(data)
        if (result is Result.Success<*>) {
            Log.d("PatientViewModel", "Addition Success(Meds)")
            val resultData = (result as Result.Success<MedsData>).data
            actionResult.setValue(ActionResult(DataView("Added: " + resultData.medsName)))
        } else actionResult.setValue(ActionResult(R.string.action_main_rcv_add_error))
    }

    fun modifyMedsData(target: MedsData, changed: MedsData) {
        val result = patientRepository?.modifyMedsData(target, changed)
        if (result is Result.Success<*>) {
            Log.d("PatientViewModel", "Modification Success(Meds)")
            val resultData = (result as Result.Success<MedsData>).data
            actionResult.setValue(ActionResult(DataView("Modified: " + resultData.medsName)))
        } else actionResult.setValue(ActionResult(R.string.action_main_rcv_modify_error))
    }

    fun deleteMedsData(target: MedsData) {
        val result = patientRepository?.deleteMedsData(target)
        if (result is Result.Success<*>) {
            Log.d("PatientViewModel", "Deletion Success(Meds)")
            val resultData = (result as Result.Success<MedsData>).data
            actionResult.setValue(ActionResult(DataView("Deleted: " + resultData.medsName)))
        } else {
            actionResult.setValue(ActionResult(R.string.action_main_rcv_delete_error))
        }
    }

    fun searchMedsData(target: MedsData) {
        val result = patientRepository?.searchMedsData(target)
        if (result is Result.Success<*>) {
            Log.d("PatientViewModel", "Search Success(Meds)")
            val resultData = (result as Result.Success<MedsData>).data
            actionResult.setValue(ActionResult(DataView("Searched Timer: " + resultData.medsName)))
        } else actionResult.setValue(ActionResult(R.string.action_main_rcv_search_error))
    }
}