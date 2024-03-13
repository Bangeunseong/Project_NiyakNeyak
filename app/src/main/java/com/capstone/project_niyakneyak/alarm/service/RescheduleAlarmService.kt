package com.capstone.project_niyakneyak.alarm.service

import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class RescheduleAlarmService : LifecycleService() {
    private lateinit var firestore: FirebaseFirestore
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        firestore = Firebase.firestore
        firestore.collection("alarms")
            .whereEqualTo(Alarm.FIELD_IS_STARTED, true).get().addOnSuccessListener {
                for(snapshot in it.documents){
                    val alarm = snapshot.toObject<Alarm>() ?: continue
                    alarm.scheduleAlarm(applicationContext)
                }
            }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }
}