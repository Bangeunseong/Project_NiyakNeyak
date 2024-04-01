package com.capstone.project_niyakneyak.data.medication_model

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@IgnoreExtraProperties
data class MedsData(
    var medsID: Int = -1,
    var medsName: String? = null,
    var dailyAmount: Int = 0,
    var medsDetail: String? = null,
    var medsStartDate: String? = null,
    var medsEndDate: String? = null,
    @ServerTimestamp
    var timeStamp: Date? = null){

    companion object {
        const val COLLECTION_ID = "medications"
        const val FIELD_ID = "medsID"
        const val FIELD_NAME = "medsName"
        const val FIELD_DAILY_AMOUNT = "dailyAmount"
        const val FIELD_DETAIL = "medsDetail"
        const val FIELD_START_DATE = "medsStartDate"
        const val FIELD_END_DATE = "medsEndDate"
        const val FIELD_REGISTERED_TIME = "timeStamp"
    }
}