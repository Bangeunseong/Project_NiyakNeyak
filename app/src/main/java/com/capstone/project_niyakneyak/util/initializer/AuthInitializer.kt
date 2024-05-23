package com.capstone.project_niyakneyak.util.initializer

import android.content.Context
import androidx.startup.Initializer
import com.google.firebase.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class AuthInitializer : Initializer<FirebaseAuth> {
    private val AUTH_EMULATOR_HOST = "10.0.2.2"
    private val AUTH_EMULATOR_PORT = 9099
    override fun create(context: Context): FirebaseAuth {
        val firebaseAuth = Firebase.auth
        // Use emulators only in debug builds
        if (BuildConfig.DEBUG) {
            firebaseAuth.useEmulator(AUTH_EMULATOR_HOST, AUTH_EMULATOR_PORT)
        }
        return firebaseAuth
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}