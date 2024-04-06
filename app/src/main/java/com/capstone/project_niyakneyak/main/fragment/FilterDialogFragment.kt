package com.capstone.project_niyakneyak.main.fragment

import androidx.fragment.app.DialogFragment
import com.capstone.project_niyakneyak.main.etc.Filters

//TODO: Modify Later
class FilterDialogFragment: DialogFragment() {
    interface FilterListener{
        fun onFilter(filters: Filters)
    }

    private val filterListener: FilterListener? = null

}