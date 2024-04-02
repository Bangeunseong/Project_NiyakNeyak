package com.capstone.project_niyakneyak.data.history_model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class HistoryData(
    var id: Long, var medsName: String? = null,
    var medsDetail: String? = null, var medsStartDate: String? = null,
    var medsEndDate: String? = null, var hour: Int,
    var min: Int, var alarmTitle: String? = null) : Parcelable {

    constructor(parcel: Parcel) :
            this(-1,null,null,
                null,null,
                0,0,null) {
        id = parcel.readLong()
        medsName = parcel.readString()
        medsDetail = parcel.readString()
        medsStartDate = parcel.readString()
        medsEndDate = parcel.readString()
        hour = parcel.readInt()
        min = parcel.readInt()
        alarmTitle = parcel.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(medsName)
        dest.writeString(medsDetail)
        dest.writeString(medsStartDate)
        dest.writeString(medsEndDate)
        dest.writeInt(hour)
        dest.writeInt(min)
        dest.writeString(alarmTitle)
    }

    companion object CREATOR : Parcelable.Creator<HistoryData> {
        override fun createFromParcel(parcel: Parcel): HistoryData {
            return HistoryData(parcel)
        }

        override fun newArray(size: Int): Array<HistoryData?> {
            return arrayOfNulls(size)
        }
    }
}