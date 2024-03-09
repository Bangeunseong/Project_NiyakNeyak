package com.capstone.project_niyakneyak.data.patient_model

import android.os.Parcel
import android.os.Parcelable
import kotlin.properties.Delegates

class MedsData() : Parcelable {
    //Field
    var id by Delegates.notNull<Long>()
    @JvmField
    var medsName: String? = null
    @JvmField
    var medsDetail: String? = null
    @JvmField
    var medsStartDate: String? = null
    @JvmField
    var medsEndDate: String? = null

    //Useful Functions for AlarmData Configuration
    @JvmField
    var alarms = mutableListOf<Int>()

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        medsName = parcel.readString()
        medsDetail = parcel.readString()
        medsStartDate = parcel.readString()
        medsEndDate = parcel.readString()
        alarms = parcel.readArrayList(ClassLoader.getSystemClassLoader()) as MutableList<Int>
    }

    //Constructor
    constructor(id: Long, medsName: String, medsDetail: String?) : this() {
        this.id = id
        this.medsName = medsName
        this.medsDetail = medsDetail
        alarms = ArrayList()
    }

    constructor(id: Long, medsName: String, medsDetail: String?,
                medsStartDate: String?, medsEndDate: String?) : this() {
        this.id = id
        this.medsName = medsName
        this.medsDetail = medsDetail
        this.medsStartDate = medsStartDate
        this.medsEndDate = medsEndDate
        alarms = ArrayList()
    }

    override fun equals(obj: Any?): Boolean {
        return if (obj is MedsData) {
            id == obj.id
        } else false
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(medsName)
        parcel.writeString(medsDetail)
        parcel.writeString(medsStartDate)
        parcel.writeString(medsEndDate)
        parcel.writeArray(arrayOf(alarms))
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun hashCode(): Int {
        var result = medsName.hashCode()
        result = 31 * result + (medsDetail?.hashCode() ?: 0)
        result = 31 * result + (medsStartDate?.hashCode() ?: 0)
        result = 31 * result + (medsEndDate?.hashCode() ?: 0)
        return result
    }

    companion object CREATOR : Parcelable.Creator<MedsData> {
        override fun createFromParcel(parcel: Parcel): MedsData {
            return MedsData(parcel)
        }

        override fun newArray(size: Int): Array<MedsData?> {
            return arrayOfNulls(size)
        }
    }
}