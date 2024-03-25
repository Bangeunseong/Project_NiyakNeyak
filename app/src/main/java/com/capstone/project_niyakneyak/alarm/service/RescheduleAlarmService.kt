package com.capstone.project_niyakneyak.alarm.service

import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.Firebase
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.firestore

class RescheduleAlarmService : LifecycleService() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        firestore = Firebase.firestore
        firebaseAuth = Firebase.auth

        if(firebaseAuth.currentUser != null){
            firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid).collection(Alarm.COLLECTION_ID)
                .where(Filter.equalTo(Alarm.FIELD_IS_STARTED, true)).get().addOnSuccessListener {
                    for(snapshot in it.documents){
                        val alarm = snapshot.toObject<Alarm>() ?: continue
                        alarm.scheduleAlarm(applicationContext)
                    }
                }.addOnFailureListener { Log.w("RescheduleService", "Reschedule Failed: $it") }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }
}