package com.capstone.project_niyakneyak.login.viewmodel

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.login.etc.LoginFormState
import com.capstone.project_niyakneyak.login.etc.LoginResult

class LoginViewModel : ViewModel() {
    val loginFormState = MutableLiveData<LoginFormState?>()
    val loginResult = MutableLiveData<LoginResult?>()

    fun loginDataChanged(username: String?, password: String) {
        if (!isUserNameValid(username) && !isPasswordValid(password)) {
            val errorCode =
                if (password.length <= 5) R.string.invalid_password_less else R.string.invalid_password_more
            loginFormState.setValue(LoginFormState(R.string.invalid_username, errorCode))
        } else if (!isUserNameValid(username)) {
            loginFormState.setValue(LoginFormState(R.string.invalid_username, null))
        } else if (!isPasswordValid(password)) {
            val errorCode =
                if (password.length <= 5) R.string.invalid_password_less else R.string.invalid_password_more
            loginFormState.setValue(LoginFormState(null, errorCode))
        } else {
            loginFormState.setValue(LoginFormState(true))
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String?): Boolean {
        if (username == null) {
            return false
        }
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            !username.trim { it <= ' ' }.isEmpty() && username.trim { it <= ' ' }.length <= 30
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String?): Boolean {
        return password != null && password.trim { it <= ' ' }.length > 5 && password.trim { it <= ' ' }.length <= 20
    }
}