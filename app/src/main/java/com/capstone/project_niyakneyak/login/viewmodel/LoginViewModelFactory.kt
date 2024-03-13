package com.capstone.project_niyakneyak.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.capstone.project_niyakneyak.data.patient_resource.LoginDataSource
import com.capstone.project_niyakneyak.data.patient_resource.LoginRepository.Companion.getInstance

class LoginViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            LoginViewModel(getInstance(LoginDataSource())) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}