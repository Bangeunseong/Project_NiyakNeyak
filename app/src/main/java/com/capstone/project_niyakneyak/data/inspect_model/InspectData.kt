package com.capstone.project_niyakneyak.data.inspect_model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import org.json.JSONObject
import java.util.Date

@IgnoreExtraProperties
data class InspectData(
    var classCode: String? = null,
    var typeName: String? = null,
    var mixType: String? = null,
    var ingrCode: String? = null,
    var ingrName: String? = null,
    var ingrEngName: String? = null,
    var ingrEngNameFull: String? = null,
    var mixIngr: String? = null,
    var formName: String? = null,
    var itemSeq: String? = null,
    var itemName: String? = null,
    var itemPermitDate: String? = null,
    var entpName: String? = null,
    var chart: String? = null,
    var className: String? = null,
    var etcOtcName: String? = null,
    var mainIngr: String? = null,
    var notificationDate: String? = null,
    var prohbtContent: String? = null,
    var remark: String? = null,
    var changeDate: String? = null): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(classCode)
        parcel.writeString(typeName)
        parcel.writeString(mixType)
        parcel.writeString(ingrCode)
        parcel.writeString(ingrName)
        parcel.writeString(ingrEngName)
        parcel.writeString(ingrEngNameFull)
        parcel.writeString(mixIngr)
        parcel.writeString(formName)
        parcel.writeString(itemSeq)
        parcel.writeString(itemName)
        parcel.writeString(itemPermitDate)
        parcel.writeString(entpName)
        parcel.writeString(chart)
        parcel.writeString(className)
        parcel.writeString(etcOtcName)
        parcel.writeString(mainIngr)
        parcel.writeString(notificationDate)
        parcel.writeString(prohbtContent)
        parcel.writeString(remark)
        parcel.writeString(changeDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<InspectData> {
        const val FIELD_TYPE_NAME = "TYPE_NAME"
        const val FIELD_CLASS_CODE = "CLASS_CODE"
        const val FIELD_MIX_TYPE = "MIX_TYPE"
        const val FIELD_INGR_CODE = "INGR_CODE"
        const val FIELD_INGR_NAME = "INGR_NAME"
        const val FIELD_INGR_ENG_NAME = "INGR_ENG_NAME"
        const val FIELD_INGR_ENG_NAME_FULL = "INGR_ENG_NAME_FULL"
        const val FIELD_MIX_INGR = "MIX_INGR"
        const val FIELD_FORM_NAME = "FORM_NAME"
        const val FIELD_ITEM_SEQ = "ITEM_SEQ"
        const val FIELD_ITEM_NAME = "ITEM_NAME"
        const val FIELD_ITEM_PERMIT_DATE = "ITEM_PERMIT_DATE"
        const val FIELD_ENTP_NAME = "ENTP_NAME"
        const val FIELD_CHART = "CHART"
        const val FIELD_CLASS_NAME = "CLASS_NAME"
        const val FIELD_ETC_OTC_NAME = "ETC_OTC_NAME"
        const val FILED_MAIN_INGR = "MAIN_INGR"
        const val FILED_NOTIFICATION_DATE = "NOTIFICATION_DATE"
        const val FIELD_PROHIBIT_CONTENT = "PROHBT_CONTENT"
        const val FILED_REMARK = "REMARK"
        const val FIELD_CHANGE_DATE = "CHANGE_DATE"
        const val COLLECTION_ID = "inspect_collection"
        const val DOCUMENT_ID = "inspect_document"
        const val PARAM_CHANGE_DOCUMENT_ID = "param_changed"

        override fun createFromParcel(parcel: Parcel): InspectData {
            return InspectData(parcel)
        }

        override fun newArray(size: Int): Array<InspectData?> {
            return arrayOfNulls(size)
        }
    }
}