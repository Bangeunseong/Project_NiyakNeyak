package com.capstone.project_niyakneyak.main.etc

import android.content.Context
import android.text.TextUtils
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.medication_model.MedicineData
import com.google.firebase.firestore.Query
import java.lang.StringBuilder

//TODO: Need Modification When data model changed!
class Filters {
    private var _startDate: String? = null
    private var _endDate: String? = null
    private var _sortBy: String? = null
    private var _searchBy: String? = null
    private var _sortDirection: Query.Direction = Query.Direction.ASCENDING

    var startDate set(value) {_startDate = value} get() = _startDate
    var endDate set(value) {_endDate = value} get() = _endDate
    var searchBy set(value) {_searchBy = value} get() = _searchBy
    var sortBy set(value) {_sortBy = value} get() = _sortBy
    var sortDirection set(value) {_sortDirection = value} get() = _sortDirection

    fun hasStartDate(): Boolean{
        return !TextUtils.isEmpty(startDate)
    }

    fun hasEndDate(): Boolean{
        return !TextUtils.isEmpty(endDate)
    }

    fun hasSortBy(): Boolean{
        return !TextUtils.isEmpty(sortBy)
    }

    fun hasSearchBy() : Boolean{
        return !TextUtils.isEmpty(searchBy)
    }

    fun getSearchDescription(context: Context): String{
        val builder = StringBuilder()

        if(sortBy == null){
            builder.append("<b>")
            builder.append(context.getString(R.string.all_medications))
            builder.append("</b>")
        }

        if(sortBy != null) {
            builder.append("<b>")
            builder.append(sortBy)
            builder.append("</b>")
        }

        if(startDate != null || endDate != null){
            builder.append(" between ")
        }

        if(startDate != null || endDate != null){
            if(startDate != null)
                builder.append("<b>").append(startDate).append("</b>")
            else builder.append("_")
            if(endDate != null)
                builder.append(" ~ ").append("<b>").append(endDate).append("</b>")
            else builder.append(" ~ _")
        }

        if(searchBy == null){
            builder.append(" search by Medicine Name")
        }

        if(searchBy != null){
            builder.append(" search by ").append(searchBy)
        }

        return builder.toString()
    }

    fun getOrderDescription(context: Context): String {
        return when (sortBy) {
            MedicineData.FIELD_ITEM_NAME_FB -> context.getString(R.string.sorted_by_medication_name)
            MedicineData.FIELD_ENPT_NAME_FB -> context.getString(R.string.sorted_by_medication_entp)
            else -> context.getString(R.string.sorted_by_recently_update)
        }
    }

    companion object {
        val default: Filters
            get(){
                val filters = Filters()
                filters.searchBy = MedicineData.FIELD_ITEM_NAME
                filters.sortBy = MedicineData.FIELD_TIME_STAMP_FB
                filters.sortDirection = Query.Direction.ASCENDING

                return filters
            }
    }
}