package com.capstone.project_niyakneyak

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate() {
        super.onCreate()
        createNotificationChannelAlarm()
        sharedPreferences = getSharedPreferences(
            PreferenceManager.getDefaultSharedPreferencesName(this),
            MODE_PRIVATE
        )
        val dn = sharedPreferences.getBoolean(getString(R.string.dayNightTheme), true)
        if (dn) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    private fun createNotificationChannelAlarm() {
        Log.d("Notification Channel", "Channel_Created!")
        val serviceChannel = NotificationChannel(CHANNEL_ID, getString(R.string.app_name) + " Service Channel", NotificationManager.IMPORTANCE_HIGH)
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }

    companion object {
        const val CHANNEL_ID = "ALARM_SERVICE_CHANNEL"
    }
}