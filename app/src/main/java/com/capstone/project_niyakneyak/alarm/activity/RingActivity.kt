package com.capstone.project_niyakneyak.alarm.activity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.databinding.ActivityRingBinding
import com.capstone.project_niyakneyak.alarm.service.AlarmService
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.util.Calendar
import java.util.Random

class RingActivity : AppCompatActivity() {
    private val snoozeTime =
        ArrayList(mutableListOf("Five Minutes", "Ten Minutes", "Fifteen Minutes"))
    private var snoozeVal = 0
    private var _binding: ActivityRingBinding? = null
    private val binding get() = _binding!!
    private var alarm: Alarm? = null

    private var _firestore: FirebaseFirestore? = null
    private val firestore get() = _firestore!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityRingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        _firestore = Firebase.firestore

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            )
        }

        val bundle = intent.getBundleExtra(getString(R.string.arg_alarm_bundle_obj))
        if (bundle != null) {
            alarm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(getString(R.string.arg_alarm_obj), Alarm::class.java)
            } else {
                @Suppress("DEPRECATION")
                bundle.getParcelable(getString(R.string.arg_alarm_obj))
            }
        }
        binding.alarmRingSnoozeTime.adapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_dropdown_item, snoozeTime)
        binding.alarmRingSnoozeTime.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    snoozeVal = (position + 1) * 5
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    parent.setSelection(0)
                    snoozeVal = 5
                }
            }
        binding.alarmRingReschedule.setOnClickListener { rescheduleAlarm() }
        binding.alarmRingOff.setOnClickListener { dismissAlarm() }
        animateClock()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _firestore = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(false)
            setTurnScreenOn(false)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON)
        }
    }

    private fun animateClock() {
        val rotateAnimation =
            ObjectAnimator.ofFloat(binding.alarmRingClock, "rotation", 0f, 30f, 0f, -30f, 0f)
        rotateAnimation.repeatCount = ValueAnimator.INFINITE
        rotateAnimation.duration = 800
        rotateAnimation.start()
    }

    private fun rescheduleAlarm() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.MINUTE, snoozeVal)
        if (alarm != null) {
            alarm!!.hour = calendar[Calendar.HOUR_OF_DAY]
            alarm!!.min = calendar[Calendar.MINUTE]
            alarm!!.title = "Snooze " + alarm!!.title
        } else {
            alarm = Alarm(
                Random().nextInt(Int.MAX_VALUE),
                calendar[Calendar.HOUR_OF_DAY],
                calendar[Calendar.MINUTE],
                true,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                "Snooze",
                RingtoneManager.getActualDefaultRingtoneUri(baseContext, RingtoneManager.TYPE_ALARM)
                    .toString(),
                false
            )
        }
        alarm!!.scheduleAlarm(applicationContext)
        val intentService = Intent(applicationContext, AlarmService::class.java)
        applicationContext.stopService(intentService)
        finish()
    }

    private fun dismissAlarm() {
        if (alarm != null) {
            val alarmsRef = firestore.collection("alarms").document(alarm!!.alarmCode.toString())
            if (!alarm!!.isRecurring) {
                alarm!!.cancelAlarm(baseContext)
                alarmsRef.update(Alarm.FIELD_IS_STARTED, false)
                    .addOnSuccessListener { Log.w(TAG,"Successfully Updated Alarm") }
                    .addOnFailureListener { Log.w(TAG, "Failed to Update Alarm") }
            }
        }
        val intentService = Intent(applicationContext, AlarmService::class.java)
        applicationContext.stopService(intentService)
        finish()
    }

    companion object{
        private const val TAG = "RING_ACTIVITY"
    }
}