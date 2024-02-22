package com.capstone.project_niyakneyak;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

public class App extends Application {
    public static final String CHANNEL_ID = "ALARM_SERVICE_CHANNEL";
    SharedPreferences sharedPreferences;
    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel_Alarm();

        sharedPreferences = getSharedPreferences(PreferenceManager.getDefaultSharedPreferencesName(this), MODE_PRIVATE);
        boolean dn = sharedPreferences.getBoolean(getString(R.string.dayNightTheme), true);
        if (dn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    private void createNotificationChannel_Alarm() {
        Log.d("Notification Channel", "Channel_Created!");
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                getString(R.string.app_name) + " Service Channel",
                NotificationManager.IMPORTANCE_HIGH
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }
}
