package com.capstone.project_niyakneyak.data.inspect_model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class UsageJointData(
    var ingrCode: String? = null,
    var ingrKorName: String? = null,
    var ingrEngName: String? = null,
    var typeName: String? = null,
    var mixIngr: String? = null,
    var itemSeq: String? = null,
    var itemName: String? = null,
    var entpName: String? = null,
    var mainIngr: String? = null,
    var mixtureIngrCode: String? = null,
    var mixtureIngrKorName: String? = null,
    var mixtureIngrEngName: String? = null,
    var mixtureItemSeq: String? = null,
    var mixtureItemName: String? = null,
    var mixtureEntpName: String? = null,
    var mixtureMainIngr: String? = null,
    var notificationDate: String? = null,
    var prohbtContent: String? = null,
    var remark: String? = null,
    var itemPermitDate: String? = null,
    var mixtureItemPermitDate: String? = null
) {
    companion object {
        const val FIELD_INGR_CODE = "INGR_CODE"
        const val FIELD_INGR_KOR_NAME = "INGR_KOR_NAME"
        const val FIELD_INGR_ENG_NAME = "INGR_ENG_NAME"
        const val FIELD_TYPE_NAME = "TYPE_NAME"
        const val FIELD_MIX_INGR = "MIX_INGR"
        const val FIELD_ITEM_SEQ = "ITEM_SEQ"
        const val FIELD_ITEM_NAME = "ITEM_NAME"
        const val FIELD_ENTP_NAME = "ENTP_NAME"
        const val FIELD_MAIN_INGR = "MAIN_INGR"
        const val FIELD_MIXTURE_INGR_CODE = "MIXTURE_INGR_CODE"
        const val FIELD_MIXTURE_INGR_KOR_NAME = "MIXTURE_INGR_KOR_NAME"
        const val FIELD_MIXTURE_INGR_ENG_NAME = "MIXTURE_INGR_ENG_NAME"
        const val FIELD_MIXTURE_ITEM_SEQ = "MIXTURE_ITME_SEQ"
        const val FIELD_MIXTURE_ITEM_NAME = "MIXTURE_ITEM_NAME"
        const val FIELD_MIXTURE_ENTP_NAME = "MIXTURE_ENTP_NAME"
        const val FIELD_MIXTURE_MAIN_INGR = "MIXTURE_MAIN_INGR"
        const val FIELD_NOTIFICATION_DATE = "NOTIFICATION_DATE"
        const val FIELD_PROHBT_CONTENT = "PROHBT_CONTENT"
        const val FIELD_REMARK = "REMARK"
        const val FIELD_ITEM_PERMIT_DATE = "ITEM_PERMIT_DATE"
        const val FIELD_MIXTURE_ITEM_PERMIT_DATE = "MIXTURE_ITEM_PERMIT_DATE"
        const val COLLECTION_ID = "inspect_documents"
    }
}
