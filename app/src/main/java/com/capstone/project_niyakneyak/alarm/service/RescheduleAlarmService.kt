package com.capstone.project_niyakneyak.alarm.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.capstone.project_niyakneyak.App
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.data.alarm_valid_model.AlarmV
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.login.activity.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.Firebase
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.firestore

class RescheduleAlarmService : LifecycleService() {
    private var _firestore: FirebaseFirestore? = null
    private var _firebaseAuth: FirebaseAuth? = null
    private val firestore get() = _firestore!!
    private val firebaseAuth get() = _firebaseAuth!!

    override fun onCreate() {
        super.onCreate()

        _firestore = Firebase.firestore
        _firebaseAuth = Firebase.auth
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if(firebaseAuth.currentUser != null){
            var notification: Notification
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            // Start Foreground Service
            val processNotification = NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentTitle("Project_NiyakNeyak")
                .setContentText("Currently Rescheduling Alarms!")
                .setSmallIcon(R.drawable.ic_timer_addition)
                .setSound(null)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .build()
            startForeground(3, processNotification)

            firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid).collection(Alarm.COLLECTION_ID)
                .where(Filter.equalTo(Alarm.FIELD_IS_STARTED, true)).get()
                .addOnSuccessListener {
                    val alarmV = AlarmV(alarmCode = 1, isStarted = true)
                    alarmV.scheduleAlarm(applicationContext)

                    for(snapshot in it.documents){
                        val alarm = snapshot.toObject<Alarm>() ?: continue
                        alarm.scheduleAlarm(applicationContext)
                    }

                    notification = NotificationCompat.Builder(this, App.CHANNEL_ID)
                        .setContentTitle("Project_NiyakNeyak")
                        .setContentText("Alarms rescheduled!")
                        .setSmallIcon(R.drawable.ic_timer_addition)
                        .setSound(null)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setAutoCancel(true)
                        .build()
                    notificationManager.notify(2, notification)
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }.addOnFailureListener {
                    Log.w("RescheduleService", "Reschedule Failed: $it")
                    notification = NotificationCompat.Builder(this, App.CHANNEL_ID)
                        .setContentTitle("Project_NiyakNeyak")
                        .setContentText("Failed to reschedule Alarms")
                        .setSmallIcon(R.drawable.ic_timer_addition)
                        .setSound(null)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setAutoCancel(true)
                        .build()
                    notificationManager.notify(2, notification)
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
        } else{
            val notificationIntent = Intent(this, LoginActivity::class.java)
            notificationIntent.putExtra("request_token", 1)
            val pendingIntent =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
                else PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val notification = NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentTitle("Project_NiyakNeyak")
                .setContentText("Login to set alarms for medications!")
                .setSmallIcon(R.drawable.ic_timer_addition)
                .setSound(null)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setFullScreenIntent(pendingIntent, true)
                .build()
            startForeground(2, notification)
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onDestroy() {
        super.onDestroy()

        _firestore = null
        _firebaseAuth = null
    }
}