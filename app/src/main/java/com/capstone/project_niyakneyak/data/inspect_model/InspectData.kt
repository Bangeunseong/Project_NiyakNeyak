package com.capstone.project_niyakneyak.data.inspect_model

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import org.json.JSONObject
import java.util.Date

@IgnoreExtraProperties
data class InspectData(
    var jsonObject: JSONObject? = null,
    @ServerTimestamp
    var timestamp: Date? = null) {

    companion object {
        const val COLLECTION_ID = "inspect_documents"
        const val PARAM_CHANGE_DOCUMENT_ID = "param_changed"
    }
}