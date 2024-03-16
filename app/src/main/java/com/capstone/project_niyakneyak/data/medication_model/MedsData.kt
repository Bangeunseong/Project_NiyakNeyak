package com.capstone.project_niyakneyak.data.medication_model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class MedsData(
    var medsID: Int = -1,
    var medsName: String? = null,
    var medsDetail: String? = null,
    var medsStartDate: String? = null,
    var medsEndDate: String? = null) : Parcelable {

    constructor(parcel: Parcel) : this() {
        medsID = parcel.readInt()
        medsName = parcel.readString()
        medsDetail = parcel.readString()
        medsStartDate = parcel.readString()
        medsEndDate = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(medsID)
        parcel.writeString(medsName)
        parcel.writeString(medsDetail)
        parcel.writeString(medsStartDate)
        parcel.writeString(medsEndDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MedsData> {
        const val FIELD_ID = "medsID"
        const val FIELD_NAME = "medsName"
        const val FIELD_DETAIL = "medsDetail"
        const val FIELD_START_DATE = "medsStartDate"
        const val FIELD_END_DATE = "medsEndDate"

        override fun createFromParcel(parcel: Parcel): MedsData {
            return MedsData(parcel)
        }

        override fun newArray(size: Int): Array<MedsData?> {
            return arrayOfNulls(size)
        }
    }
}