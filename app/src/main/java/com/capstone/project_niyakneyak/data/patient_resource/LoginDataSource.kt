package com.capstone.project_niyakneyak.data.patient_resource

import com.capstone.project_niyakneyak.data.Result
import com.capstone.project_niyakneyak.data.patient_model.SignInUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.IOException
import java.util.UUID

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {
    private var auth: FirebaseAuth = Firebase.auth

    fun signIn(username: String?, password: String?): Result<Any?> {
        return try {
            // TODO: handle loggedInUser authentication
            val fakeUser = SignInUser(UUID.randomUUID().toString(),"Jane Doe")
            return (Result.Success(fakeUser))
        } catch (e: Exception) {
            Result.Error(IOException("Error logging in", e))
        }
    }

    fun signOut() {
        // TODO: revoke authentication
    }
}