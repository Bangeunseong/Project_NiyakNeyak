package com.capstone.project_niyakneyak.data.medication_model

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@IgnoreExtraProperties
data class MedicineHistoryData(
    var medsID: Int = -1,
    var dailyAmount: Int = 0,
    var itemName: String? = null,
    var itemEngName: String? = null,
    var bigPrdtImgUrl: String? = null,
    @ServerTimestamp
    var timeStamp: Date? = null) {

    companion object{
        const val COLLECTION_ID = "history"
        const val FIELD_TIME_STAMP = "timeStamp"
    }
}