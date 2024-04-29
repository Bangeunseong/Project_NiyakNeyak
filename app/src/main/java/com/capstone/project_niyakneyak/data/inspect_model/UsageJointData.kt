package com.capstone.project_niyakneyak.data.inspect_model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class UsageJointData(
    var ingrCode: String? = null,
    var ingrKorName: String? = null,
    var ingrEngName: String? = null,
    var durSeq: String? = null,
    var typeCode: String? = null,
    var typeName: String? = null,
    var mix: String? = null,
    var mixIngr: String? = null,
    var itemSeq: String? = null,
    var itemName: String? = null,
    var entpName: String? = null,
    var chart: String? = null,
    var formCode: String? = null,
    var formName: String? = null,
    var etcOtcName: String? = null,
    var className: String? = null,
    var classCode: String? = null,
    var mainIngr: String? = null,
    var mixtureDurSeq: String? = null,
    var mixtureMix: String? = null,
    var mixtureIngrCode: String? = null,
    var mixtureIngrKorName: String? = null,
    var mixtureIngrEngName: String? = null,
    var mixtureItemSeq: String? = null,
    var mixtureItemName: String? = null,
    var mixtureEntpName: String? = null,
    var mixtureFormCode: String? = null,
    var mixtureEtcOtcCode: String? = null,
    var mixtureClassName: String? = null,
    var mixtureMainIngr: String? = null,
    var notificationDate: String? = null,
    var prohbtContent: String? = null,
    var itemPermitDate: String? = null,
    var mixtureItemPermitDate: String? = null,
    var mixtureChart: String? = null
) {
    companion object {
        const val COLLECTION_ID = "inspect_documents"
    }
}
