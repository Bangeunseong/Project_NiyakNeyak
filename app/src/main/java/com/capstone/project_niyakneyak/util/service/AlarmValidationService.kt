package com.capstone.project_niyakneyak.util.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.capstone.project_niyakneyak.App
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.alarm_valid_model.AlarmV
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class AlarmValidationService: Service() {
    // Params for Firebase Access
    private var _firestore: FirebaseFirestore? = null
    private var _firebaseAuth: FirebaseAuth? = null
    private val firestore get() = _firestore!!
    private val firebaseAuth get() = _firebaseAuth!!

    // Validation Alarm Data
    private var alarm: AlarmV? = null

    // Query
    private var query: Query? = null

    override fun onCreate() {
        super.onCreate()

        _firestore = Firebase.firestore
        _firebaseAuth = Firebase.auth
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val bundle = intent.getBundleExtra(getString(R.string.arg_alarm_bundle_obj))

        alarm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle?.getParcelable(getString(R.string.arg_alarm_obj), AlarmV::class.java)
        } else {
            @Suppress("DEPRECATION")
            bundle?.getParcelable(getString(R.string.arg_alarm_obj))
        }

        val processNotification = NotificationCompat.Builder(this, App.CHANNEL_ID)
            .setContentTitle("Project_NiyakNeyak")
            .setContentText("Currently Validating Alarms")
            .setSmallIcon(R.drawable.ic_timer_addition)
            .setSound(null)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .build()
        startForeground(4, processNotification)



        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()

        _firestore = null
        _firebaseAuth = null
    }
}