package com.capstone.project_niyakneyak.data.patient_resource

import androidx.lifecycle.MutableLiveData
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.Result
import com.capstone.project_niyakneyak.data.patient_model.MedsData
import com.capstone.project_niyakneyak.data.patient_model.PatientData
import com.capstone.project_niyakneyak.ui.main.etc.ActionResult
import com.capstone.project_niyakneyak.ui.main.etc.DataView
import java.io.Serializable

class PatientRepository private constructor(private val dataSource: PatientDataSource) :
    Serializable {
    private var patientData: PatientData? = null
    val actionResult = MutableLiveData<ActionResult>()

    init {
        if (patientData == null) {
            val resultRead = dataSource.readPatientData()
            patientData =
                if (resultRead is Result.Success<*>) (resultRead as Result.Success<PatientData?>).data else PatientData()
        }
    }

    //Getter for patient Info.
    fun getPatientData(): Result<Any?> {
        val result = dataSource.readPatientData()
        return if (result is Result.Success<*>) result else {
            if (patientData != null) {
                Result.Success(patientData)
            } else Result.Error(Exception("Cannot get data from remote and repository"))
        }
    }

    //Useful Functions to search, add, modify, delete patient medication Info.
    fun searchMedsData(data: MedsData): Result<Any?> {
        val result = dataSource.searchMedsData(data)
        return if (result is Result.Success<*>) {
            actionResult.value = ActionResult(DataView("Searching MedsData Success"))
            result
        } else {
            if (patientData!!.searchMedsData(data)) {
                actionResult.value = ActionResult(DataView("Searching MedsData Success"))
                return Result.Success(data)
            }
            actionResult.value = ActionResult(R.string.action_main_rcv_search_error)
            Result.Error(Exception("Data does not exists!"))
        }
    }

    fun addMedsData(data: MedsData): Result<Any?> {
        val result = dataSource.writeMedsData(data, 0)
        patientData!!.addMedsData(data)
        return if (result is Result.Success<*>) {
            actionResult.value = ActionResult(DataView("Adding MedsData Success"))
            result
        } else {
            if (patientData != null) {
                actionResult.value = ActionResult(DataView("Adding MedsData Success"))
                return Result.Success(data)
            }
            actionResult.value = ActionResult(R.string.action_main_rcv_add_error)
            Result.Error(Exception("Cannot connect to database but saved in local repository"))
        }
    }

    fun modifyMedsData(originData: MedsData, changedData: MedsData): Result<Any?> {
        val result = dataSource.writeMedsData(originData, changedData)
        val flag = patientData!!.modifyMedsData(originData, changedData)
        return if (result is Result.Success<*>) {
            actionResult.value = ActionResult(DataView("Modifying MedsData Success"))
            result
        } else {
            if (flag) {
                actionResult.value = ActionResult(DataView("Modifying MedsData Success"))
                return Result.Success(changedData)
            }
            actionResult.value = ActionResult(R.string.action_main_rcv_modify_error)
            Result.Error(Exception("Data does not exists!"))
        }
    }

    fun deleteMedsData(target: MedsData): Result<Any?> {
        val result = dataSource.writeMedsData(target, 1)
        val flag = patientData!!.deleteMedsData(target)
        return if (result is Result.Success<*>) {
            actionResult.value = ActionResult(DataView("Deleting MedsData Success"))
            result
        } else if (flag) Result.Success(target) else Result.Error(
            Exception("Data does not exists!")
        )
    }

    companion object {
        @Volatile
        private var instance: PatientRepository? = null
        fun getInstance(dataSource: PatientDataSource): PatientRepository? {
            if (instance == null) {
                instance = PatientRepository(dataSource)
            }
            return instance
        }
    }
}