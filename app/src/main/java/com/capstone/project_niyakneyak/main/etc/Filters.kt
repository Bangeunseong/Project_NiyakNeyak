package com.capstone.project_niyakneyak.main.etc

import android.content.Context
import android.text.TextUtils
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.medication_model.MedsData
import com.google.firebase.firestore.Query
import java.lang.StringBuilder

class Filters {
    var category: String? = null
    var startDate: String? = null
    var endDate: String? = null
    var sortBy: String? = null
    var sortDirection: Query.Direction = Query.Direction.DESCENDING

    fun hasCategory(): Boolean{
        return !TextUtils.isEmpty(category)
    }

    fun hasStartDate(): Boolean{
        return !TextUtils.isEmpty(startDate)
    }

    fun hasEndDate(): Boolean{
        return !TextUtils.isEmpty(endDate)
    }

    fun hasSortBy(): Boolean{
        return !TextUtils.isEmpty(sortBy)
    }

    fun getSearchDescription(context: Context): String{
        val builder = StringBuilder()

        if(category == null){
            builder.append("<b>")
            builder.append(context.getString(R.string.all_medications))
            builder.append("</b>")
        }

        if(category != null){
            builder.append("<b>")
            builder.append(category)
            builder.append("</b>")
        }

        if(category != null && (startDate != null || endDate != null)){
            builder.append(" in ")
        }

        if(startDate != null || endDate != null){
            if(startDate != null)
                builder.append("<b>").append(startDate).append("</b>")
            else builder.append("_")
            if(endDate != null)
                builder.append(" ~ ").append("<b>").append(endDate).append("</b>")
            else builder.append(" ~ _")
        }

        return builder.toString()
    }

    fun getOrderDescription(context: Context): String {
        return when (sortBy) {
            MedsData.FIELD_NAME -> context.getString(R.string.sorted_by_medication_name)
            MedsData.FIELD_ID -> context.getString(R.string.sorted_by_medication_id)
            else -> context.getString(R.string.sorted_by_recently_update)
        }
    }

    companion object {
        val default: Filters
            get(){
                val filters = Filters()
                filters.sortBy = MedsData.FIELD_NAME
                filters.sortDirection = Query.Direction.DESCENDING

                return filters
            }
    }
}