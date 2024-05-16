package com.capstone.project_niyakneyak.util.service

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.capstone.project_niyakneyak.App
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.data.alarm_valid_model.AlarmV
import com.capstone.project_niyakneyak.data.medication_model.MedicineData
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import java.util.Date

class AlarmValidationService: Service() {
    // Params for Firebase Access
    private var _firestore: FirebaseFirestore? = null
    private var _firebaseAuth: FirebaseAuth? = null
    private val firestore get() = _firestore!!
    private val firebaseAuth get() = _firebaseAuth!!

    // Validation Alarm Data
    private var alarm: AlarmV? = null

    // Query
    private var queryForMeds: Query? = null
    private var queryForAlarm: Query? = null

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

        if(firebaseAuth.currentUser != null){
            val alarmSnapshot = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                .collection(Alarm.COLLECTION_ID)
            val medicineData = mutableListOf<MedicineData>()
            val medicines = mutableListOf<Int>()
            val alarms = mutableListOf<Alarm>()

            var notification: Notification
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            queryForMeds = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                .collection(MedicineData.COLLECTION_ID)

            queryForAlarm = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                .collection(Alarm.COLLECTION_ID)
                .whereArrayContainsAny(Alarm.FIELD_MEDICATION_LIST, medicines)

            queryForMeds?.let { firstQuery ->
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

                firstQuery.get().addOnSuccessListener { firstSnapshot ->
                    for(document in firstSnapshot.documents){
                        val medicine = document.toObject<MedicineData>() ?: continue
                        medicineData.add(medicine)
                        medicines.add(medicine.medsID)
                    }
                    queryForAlarm?.let { secondQuery ->
                        secondQuery.get().addOnSuccessListener { secondSnapshot ->
                            for(document in secondSnapshot.documents){
                                val alarm = document.toObject<Alarm>() ?: continue
                                alarms.add(alarm)
                            }

                            firestore.runTransaction { transaction ->
                                for(medicine in medicineData){
                                    if(medicine.medsStartDate == null || medicine.medsEndDate == null) continue

                                    if(medicine.medsStartDate!!.before(Date(System.currentTimeMillis())) && medicine.medsEndDate!!.after(Date(System.currentTimeMillis()))){
                                        for(alarm in alarms){
                                            if(medicine.alarmList.contains(alarm.alarmCode) && !alarm.medsList.contains(medicine.medsID)){
                                                alarm.medsList.add(medicine.medsID)
                                                if(!alarm.isStarted) {
                                                    alarm.isStarted = true
                                                    alarm.scheduleAlarm(applicationContext)
                                                }
                                                transaction.update(
                                                    alarmSnapshot.document(alarm.alarmCode.toString()),
                                                    Alarm.FIELD_MEDICATION_LIST,
                                                    FieldValue.arrayUnion(medicine.medsID)
                                                )
                                            }
                                        }
                                    } else{
                                        for(alarm in alarms){
                                            if(medicine.alarmList.contains(alarm.alarmCode) && alarm.medsList.contains(medicine.medsID)){
                                                alarm.medsList.remove(medicine.medsID)
                                                if(alarm.medsList.size < 1) {
                                                    alarm.isStarted = false
                                                    alarm.cancelAlarm(applicationContext)
                                                }
                                                transaction.update(
                                                    alarmSnapshot.document(alarm.alarmCode.toString()),
                                                    Alarm.FIELD_MEDICATION_LIST,
                                                    FieldValue.arrayRemove(medicine.medsID)
                                                )
                                            }
                                        }
                                    }
                                }
                            }.addOnSuccessListener {
                                notification = NotificationCompat.Builder(this, App.CHANNEL_ID)
                                    .setContentTitle("Project_NiyakNeyak")
                                    .setContentText("Alarms Validated!")
                                    .setSmallIcon(R.drawable.ic_timer_addition)
                                    .setSound(null)
                                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                    .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                                    .setPriority(NotificationCompat.PRIORITY_MAX)
                                    .setAutoCancel(true)
                                    .build()
                                notificationManager.notify(2, notification)
                                stopForeground(STOP_FOREGROUND_DETACH)
                                stopSelf()
                            }.addOnFailureListener {
                                notification = NotificationCompat.Builder(this, App.CHANNEL_ID)
                                    .setContentTitle("Project_NiyakNeyak")
                                    .setContentText("Alarms Validation Failed!")
                                    .setSmallIcon(R.drawable.ic_timer_addition)
                                    .setSound(null)
                                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                    .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                                    .setPriority(NotificationCompat.PRIORITY_MAX)
                                    .setAutoCancel(true)
                                    .build()
                                notificationManager.notify(2, notification)
                                stopForeground(STOP_FOREGROUND_DETACH)
                                stopSelf()
                            }
                        }.addOnFailureListener {
                            notification = NotificationCompat.Builder(this, App.CHANNEL_ID)
                                .setContentTitle("Project_NiyakNeyak")
                                .setContentText("Alarms Validation Failed!")
                                .setSmallIcon(R.drawable.ic_timer_addition)
                                .setSound(null)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setAutoCancel(true)
                                .build()
                            notificationManager.notify(2, notification)
                            stopForeground(STOP_FOREGROUND_DETACH)
                            stopSelf()
                        }
                    }
                }.addOnFailureListener {
                    notification = NotificationCompat.Builder(this, App.CHANNEL_ID)
                        .setContentTitle("Project_NiyakNeyak")
                        .setContentText("Alarms Validation Failed!")
                        .setSmallIcon(R.drawable.ic_timer_addition)
                        .setSound(null)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setAutoCancel(true)
                        .build()
                    notificationManager.notify(2, notification)
                    stopForeground(STOP_FOREGROUND_DETACH)
                    stopSelf()
                }
            }
        }
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