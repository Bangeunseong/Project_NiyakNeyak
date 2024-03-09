package com.capstone.project_niyakneyak.ui.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.project_niyakneyak.data.patient_resource.LoginDataSource
import com.capstone.project_niyakneyak.data.patient_resource.LoginRepository.Companion.getInstance

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class LoginViewModelFactory : ViewModelProvider.AndroidViewModelFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            LoginViewModel(getInstance(LoginDataSource())) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}