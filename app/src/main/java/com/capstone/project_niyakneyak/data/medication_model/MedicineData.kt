package com.capstone.project_niyakneyak.data.medication_model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import java.text.SimpleDateFormat
import java.util.Date

@IgnoreExtraProperties
data class MedicineData(
    var itemSeq: String? = null,
    var itemName: String? = null,
    var itemEngName: String? = null,
    var entpName: String? = null,
    var entpEngName: String? = null,
    var entpSeq: String? = null,
    var entpNo: String? = null,
    var itemPermDate: String? = null,
    var inDuty: String? = null,
    var prdlstStrdCode: String? = null,
    var spcltyPblc: String? = null,
    var pdtType: String? = null,
    var pdtPermNo: String? = null,
    var itemIngrName: String? = null,
    var itemIngrCnt: String? = null,
    var bigPrdtImgUrl: String? = null,
    var permKindCode: String? = null,
    var cancelDate: String? = null,
    var cancelName: String? = null,
    var ediCode: String? = null,
    var bizrNo: String? = null) : Parcelable {

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

    fun convertDate(date: String): Date? {
        return SimpleDateFormat("yyyyMMdd").parse(date)
    }

    fun getMedicineDataAsString(): String {
        return "$itemSeq, $itemName, $itemEngName, $entpName, $entpEngName, " +
                "$entpSeq, $entpNo, $itemPermDate, $inDuty, $prdlstStrdCode, " +
                "$spcltyPblc, $pdtType, $pdtPermNo, $itemIngrName, $itemIngrCnt, " +
                "$bigPrdtImgUrl, $permKindCode, $cancelDate, $cancelName, $ediCode" +
                "$bizrNo"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(itemSeq)
        parcel.writeString(itemName)
        parcel.writeString(itemEngName)
        parcel.writeString(entpName)
        parcel.writeString(entpEngName)
        parcel.writeString(entpSeq)
        parcel.writeString(entpNo)
        parcel.writeString(itemPermDate)
        parcel.writeString(inDuty)
        parcel.writeString(prdlstStrdCode)
        parcel.writeString(spcltyPblc)
        parcel.writeString(pdtType)
        parcel.writeString(pdtPermNo)
        parcel.writeString(itemIngrName)
        parcel.writeString(itemIngrCnt)
        parcel.writeString(bigPrdtImgUrl)
        parcel.writeString(permKindCode)
        parcel.writeString(cancelDate)
        parcel.writeString(cancelName)
        parcel.writeString(ediCode)
        parcel.writeString(bizrNo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MedicineData> {
        const val FIELD_ITEM_SEQ = "ITEM_SEQ"
        const val FIELD_ITEM_NAME = "ITEM_NAME"
        const val FIELD_ITEM_ENG_NAME = "ITEM_ENG_NAME"
        const val FIELD_ENPT_NAME = "ENTP_NAME"
        const val FIELD_ENPT_ENG_NAME = "ENTP_ENG_NAME"
        const val FILED_ENTP_SEQ = "ENTP_SEQ"
        const val FILED_ENTP_NO = "ENTP_NO"
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

        override fun createFromParcel(parcel: Parcel): MedicineData {
            return MedicineData(parcel)
        }

        override fun newArray(size: Int): Array<MedicineData?> {
            return arrayOfNulls(size)
        }
    }
}