package com.capstone.project_niyakneyak.data.patient_resource

import com.capstone.project_niyakneyak.data.Result
import com.capstone.project_niyakneyak.data.patient_model.LoggedInUser
import java.io.IOException
import java.util.UUID

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {
    fun login(username: String?, password: String?): Result<Any?> {
        return try {
            // TODO: handle loggedInUser authentication
            val fakeUser = LoggedInUser(UUID.randomUUID().toString(),"Jane Doe")
            return (Result.Success(fakeUser))
        } catch (e: Exception) {
            Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}