package com.capstone.project_niyakneyak.main.etc

import com.google.firebase.firestore.Query

class Filters {
    var category: String? = null
    var startDate: String? = null
    var endDate: String? = null
    var sortBy: String? = null
    var sortDirection: Query.Direction = Query.Direction.DESCENDING
}