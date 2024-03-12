package com.capstone.project_niyakneyak.data.history_model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_table")
class HistoryData : Parcelable {
    @PrimaryKey
    var id: Long
    @JvmField
    var medsName: String
    @JvmField
    var medsDetail: String?
    @JvmField
    var medsStartDate: String?
    @JvmField
    var medsEndDate: String?
    @JvmField
    var hour: Int
    @JvmField
    var min: Int
    @JvmField
    var alarmTitle: String?

    constructor(
        id: Long,
        medsName: String,
        medsDetail: String?,
        medsStartDate: String?,
        medsEndDate: String?,
        hour: Int,
        min: Int,
        alarmTitle: String?
    ) {
        this.id = id
        this.medsName = medsName
        this.medsDetail = medsDetail
        this.medsStartDate = medsStartDate
        this.medsEndDate = medsEndDate
        this.hour = hour
        this.min = min
        this.alarmTitle = alarmTitle
    }

    protected constructor(`in`: Parcel) {
        id = `in`.readLong()
        medsName = `in`.readString().toString()
        medsDetail = `in`.readString()
        medsStartDate = `in`.readString()
        medsEndDate = `in`.readString()
        hour = `in`.readInt()
        min = `in`.readInt()
        alarmTitle = `in`.readString()
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