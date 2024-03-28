package com.capstone.project_niyakneyak.main.etc

import android.text.TextUtils
import com.google.firebase.firestore.Query

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


}