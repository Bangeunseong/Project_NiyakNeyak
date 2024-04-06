package com.capstone.project_niyakneyak.data.medication_model

import android.os.Parcel
import android.os.Parcelable

data class Container(
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
    var bizrNo: String? = null
):Parcelable {
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

    companion object CREATOR : Parcelable.Creator<Container> {
        const val CONTAINER_KEY = "Container"
        const val CONTAINER_BUNDLE_KEY = "Container_Bundle"
        override fun createFromParcel(parcel: Parcel): Container {
            return Container(parcel)
        }

        override fun newArray(size: Int): Array<Container?> {
            return arrayOfNulls(size)
        }
    }
}