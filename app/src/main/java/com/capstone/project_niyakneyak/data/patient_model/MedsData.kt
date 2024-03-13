package com.capstone.project_niyakneyak.data.patient_model

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class MedsData(
    var medsName: String? = null,
    var medsDetail: String? = null,
    var medsStartDate: String? = null,
    var medsEndDate: String? = null,
    var alarms: ArrayList<Int> = ArrayList()) : Parcelable {

    constructor(parcel: Parcel) : this() {
        medsName = parcel.readString()
        medsDetail = parcel.readString()
        medsStartDate = parcel.readString()
        medsEndDate = parcel.readString()
        alarms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            parcel.readArrayList(ClassLoader.getSystemClassLoader(), Int::class.java)!!
        } else parcel.readArrayList(ClassLoader.getSystemClassLoader()) as ArrayList<Int>
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(medsName)
        parcel.writeString(medsDetail)
        parcel.writeString(medsStartDate)
        parcel.writeString(medsEndDate)
        parcel.writeArray(arrayOf(alarms))
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MedsData> {
        const val FIELD_NAME = "medsName"
        const val FIELD_DETAIL = "medsDetail"
        const val FIELD_START_DATE = "medsStartDate"
        const val FIELD_END_DATE = "medsEndDate"
        const val FIELD_ALARMS = "alarms"

        override fun createFromParcel(parcel: Parcel): MedsData {
            return MedsData(parcel)
        }

        override fun newArray(size: Int): Array<MedsData?> {
            return arrayOfNulls(size)
        }
    }
}