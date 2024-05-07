package com.capstone.project_niyakneyak.data.medication_model

import android.text.TextUtils
import com.capstone.project_niyakneyak.main.etc.Converter
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@IgnoreExtraProperties
data class MedicineData(
    var itemSeq: String? = null,
    var itemName: String? = null,
    var itemEngName: String? = null,
    var entpName: String? = null,
    var entpEngName: String? = null,
    var entpSeq: String? = null,
    var entpNo: String? = null,
    var itemPermDate: Date? = null,
    var inDuty: String? = null,
    var prdlstStrdCode: String? = null,
    var spcltyPblc: String? = null,
    var pdtType: String? = null,
    var pdtPermNo: String? = null,
    var itemIngrName: String? = null,
    var itemIngrCnt: String? = null,
    var bigPrdtImgUrl: String? = null,
    var permKindCode: String? = null,
    var cancelDate: Date? = null,
    var cancelName: String? = null,
    var ediCode: String? = null,
    var bizrNo: String? = null,
    var medsID: Int = -1,
    var dailyAmount: Int = 0,
    var medsDetail: String? = null,
    var medsStartDate: Date? = null,
    var medsEndDate: Date? = null,
    val alarmList: MutableList<Int> = mutableListOf(),
    @ServerTimestamp
    var timeStamp: Date? = null) : Converter {

    override fun convertStrToDate(date: String?): Date? {
        if(TextUtils.isEmpty(date)) return null
        return SimpleDateFormat("yyyyMMdd", Locale.KOREAN).parse(date!!)
    }

    companion object {
        const val COLLECTION_ID = "medications"

        const val FIELD_ITEM_SEQ = "ITEM_SEQ"
        const val FIELD_ITEM_NAME = "ITEM_NAME"
        const val FIELD_ITEM_ENG_NAME = "ITEM_ENG_NAME"
        const val FIELD_ENPT_NAME = "ENTP_NAME"
        const val FIELD_ENPT_ENG_NAME = "ENTP_ENG_NAME"
        const val FIELD_ENPT_SEQ = "ENTP_SEQ"
        const val FIELD_ENPT_NO = "ENTP_NO"
        const val FIELD_ITEM_PERMIT_DATE = "ITEM_PERMIT_DATE"
        const val FIELD_INDUTY = "INDUTY"
        const val FIELD_PRDLST_STDR_CODE = "PRDLST_STDR_CODE"
        const val FIELD_SPCLTY_PBLC = "SPCLTY_PBLC"
        const val FIELD_PRDUCT_TYPE = "PRDUCT_TYPE"
        const val FIELD_PRDUCT_PRMISN_NO = "PRDUCT_PRMISN_NO"
        const val FIELD_ITEM_INGR_NAME = "ITEM_INGR_NAME"
        const val FIELD_ITEM_INGR_CNT = "ITEM_INGR_CNT"
        const val FIELD_BIG_PRDT_IMG_URL = "BIG_PRDT_IMG_URL"
        const val FIELD_PERMIT_KIND_CODE = "PERMIT_KIND_CODE"
        const val FIELD_CANCEL_DATE = "CANCEL_DATE"
        const val FIELD_CANCEL_NAME = "CANCEL_NAME"
        const val FIELD_EDI_CODE = "EDI_CODE"
        const val FIELD_BIZRNO = "BIZRNO"

        const val FIELD_ITEM_SEQ_FB = "itemSeq"
        const val FIELD_ITEM_NAME_FB = "itemName"
        const val FIELD_ENPT_NAME_FB = "entpName"
        const val FIELD_IN_DUTY_FB = "inDuty"
        const val FIELD_MEDICINE_ID_FB = "medsID"
        const val FIELD_DAILY_AMOUNT_FB = "dailyAmount"
        const val FIELD_START_DATE_FB = "medsStartDate"
        const val FIELD_END_DATE_FB = "medsEndDate"
        const val FIELD_ALARM_LIST_FB = "alarmList"
        const val FIELD_TIME_STAMP_FB = "timeStamp"
    }
}