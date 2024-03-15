package com.capstone.project_niyakneyak.login.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.Result
import com.capstone.project_niyakneyak.data.user_model.SignInUser
import com.capstone.project_niyakneyak.data.user_resource.LoginRepository
import com.capstone.project_niyakneyak.login.etc.LoginFormState
import com.capstone.project_niyakneyak.login.etc.LoggedInUserView
import com.capstone.project_niyakneyak.login.etc.LoginResult

class LoginViewModel internal constructor(private val loginRepository: LoginRepository?) :
    ViewModel() {
    val loginFormState = MutableLiveData<LoginFormState?>()
    val loginResult = MutableLiveData<LoginResult?>()
    fun getLoginFormState(): LiveData<LoginFormState?> {
        return loginFormState
    }

    fun getLoginResult(): LiveData<LoginResult?> {
        return loginResult
    }

    fun login(username: String?, password: String?) {
        // can be launched in a separate asynchronous job
        val result = loginRepository!!.login(username, password)
        if (result is Result.Success<*>) {
            val data = (result as Result.Success<SignInUser?>).data
            loginResult.setValue(LoginResult(LoggedInUserView(data!!.displayName)))
        } else {
            loginResult.setValue(LoginResult(R.string.login_failed))
        }
    }

    fun logout(username: String?, password: String?) {
        if (loginRepository!!.isLoggedIn) {
            loginRepository.logout()
        }
    }

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