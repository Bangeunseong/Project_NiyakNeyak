package com.capstone.project_niyakneyak.main.viewmodel

import androidx.lifecycle.ViewModel
import com.capstone.project_niyakneyak.main.etc.Filters

class DataViewModel : ViewModel() {
    // Field
    var isSignedIn: Boolean = false
    val filters: Filters = Filters.default
}