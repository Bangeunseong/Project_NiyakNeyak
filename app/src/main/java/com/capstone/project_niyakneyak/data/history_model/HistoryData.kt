package com.capstone.project_niyakneyak.data.history_model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_table")
class HistoryData : Parcelable {
    @PrimaryKey
    var id: Long

    @JvmField
    @ColumnInfo(name = "name")
    var medsName: String?

    @JvmField
    @ColumnInfo(name = "detail")
    var medsDetail: String?

    @JvmField
    @ColumnInfo(name = "start_date")
    var medsStartDate: String?

    @JvmField
    @ColumnInfo(name = "end_date")
    var medsEndDate: String?

    @JvmField
    @ColumnInfo(name = "hour")
    var hour: Int

    @JvmField
    @ColumnInfo(name = "min")
    var min: Int

    @JvmField
    @ColumnInfo(name = "alarm_title")
    var alarmTitle: String?

    constructor(
        id: Long,
        meds_name: String?,
        meds_detail: String?,
        meds_start_date: String?,
        meds_end_date: String?,
        hour: Int,
        min: Int,
        alarmTitle: String?
    ) {
        this.id = id
        this.medsName = meds_name
        this.medsDetail = meds_detail
        this.medsStartDate = meds_start_date
        this.medsEndDate = meds_end_date
        this.hour = hour
        this.min = min
        this.alarmTitle = alarmTitle
    }

    protected constructor(`in`: Parcel) {
        id = `in`.readLong()
        medsName = `in`.readString()
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