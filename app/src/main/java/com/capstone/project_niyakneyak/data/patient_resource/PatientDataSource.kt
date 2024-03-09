package com.capstone.project_niyakneyak.data.patient_resource

import android.util.Log
import com.capstone.project_niyakneyak.data.Result
import com.capstone.project_niyakneyak.data.patient_model.MedsData

class PatientDataSource {
    init {
        Log.d("PatientDataSource", "Called")
    }

    fun readPatientData(): Result<Any?> {
        //TODO: Search for patient info. by using userid
        return Result.Error(Exception("Database not connected"))
    }

    fun searchMedsData(target: MedsData?): Result<Any?> {
        //TODO: Search for medication info.
        return Result.Error(Exception("Database not connected"))
    }

    fun writeMedsData(data: MedsData?, option: Int): Result<Any?> {
        //TODO: Write added data or delete target data
        return Result.Error(Exception("Database not connected"))
    }

    fun writeMedsData(originData: MedsData?, changedData: MedsData?): Result<Any?> {
        //TODO: Modify added data
        return Result.Error(Exception("Database not connected"))
    }
}