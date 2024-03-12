package com.capstone.project_niyakneyak.ui.alarm.activity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.databinding.ActivityRingBinding
import com.capstone.project_niyakneyak.ui.alarm.service.AlarmService
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.AlarmListViewModel
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.AlarmListViewModelFactory
import java.util.Calendar
import java.util.Random

class RingActivity : AppCompatActivity() {
    private val snoozeTime =
        ArrayList(mutableListOf("Five Minutes", "Ten Minutes", "Fifteen Minutes"))
    private var snoozeVal = 0
    private var alarmListViewModel: AlarmListViewModel? = null
    private var binding: ActivityRingBinding? = null
    private var alarm: Alarm? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRingBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            )
        }
        alarmListViewModel = ViewModelProvider(this, AlarmListViewModelFactory(application))[AlarmListViewModel::class.java]

        val bundle = intent.getBundleExtra(getString(R.string.arg_alarm_bundle_obj))
        if (bundle != null) alarm = bundle.getParcelable(getString(R.string.arg_alarm_obj))
        binding!!.alarmRingSnoozeTime.adapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_dropdown_item, snoozeTime)
        binding!!.alarmRingSnoozeTime.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    snoozeVal = (position + 1) * 5
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    parent.setSelection(0)
                    snoozeVal = 5
                }
            }
        binding!!.alarmRingReschedule.setOnClickListener { rescheduleAlarm() }
        binding!!.alarmRingOff.setOnClickListener { dismissAlarm() }
        animateClock()
    }

    override fun onDestroy() {
        super.onDestroy()
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
            ObjectAnimator.ofFloat(binding!!.alarmRingClock, "rotation", 0f, 30f, 0f, -30f, 0f)
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
            if (!alarm!!.isRecurring) {
                alarm!!.isStarted = false
                alarm!!.cancelAlarm(baseContext)
                alarmListViewModel!!.update(alarm!!)
            }
        }
        val intentService = Intent(applicationContext, AlarmService::class.java)
        applicationContext.stopService(intentService)
        finish()
    }
}