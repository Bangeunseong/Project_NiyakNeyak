package com.capstone.project_niyakneyak.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchActivityViewModel: ViewModel() {
    // Field
    var currentPage: Int = 1
    val currentNumOfRows: Int = 25
    var totalItemCount: Int = 0
    val remainPage get() = totalItemCount / currentNumOfRows + if(totalItemCount % currentNumOfRows != 0) 1 else 0
    val selectedPositionObserver = MutableLiveData<Int>()
    val searchQueryObserver = MutableLiveData<String?>()
    init {
        selectedPositionObserver.value = -1
    }
}