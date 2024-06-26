package com.capstone.project_niyakneyak.data.medication_model

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@IgnoreExtraProperties
data class MedicineHistoryData(
    var medsID: Int = -1,
    var dailyAmount: Int = 0,
    var itemSeq: String? = null,
    var itemName: String? = null,
    var alarmCode: Int = 0,
    @ServerTimestamp
    var timeStamp: Date? = null) {

    companion object{
        const val COLLECTION_ID = "history"
        const val FIELD_TIME_STAMP = "timeStamp"
        const val FIELD_MEDICINE_ID = "medsID"
    }
}