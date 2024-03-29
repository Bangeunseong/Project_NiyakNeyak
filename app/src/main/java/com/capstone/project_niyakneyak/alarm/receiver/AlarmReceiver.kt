package com.capstone.project_niyakneyak.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.alarm.service.AlarmService
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {
    var alarm: Alarm? = null
    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.getBundleExtra(context.getString(R.string.arg_alarm_bundle_obj))
        if (bundle != null) {
            alarm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(context.getString(R.string.arg_alarm_obj), Alarm::class.java)
            } else {
                @Suppress("DEPRECATION")
                bundle.getParcelable(context.getString(R.string.arg_alarm_obj))
            }
        }
        val toastText = String.format("Alarm Received")
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
        if (alarm != null) {
            if (!alarm!!.isRecurring) {
                startAlarmService(context, alarm!!)
            } else {
                if (isAlarmToday(alarm!!)) {
                    startAlarmService(context, alarm!!)
                }
            }
        }
    }

    private fun isAlarmToday(alarm1: Alarm): Boolean {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val today = calendar[Calendar.DAY_OF_WEEK]
        return when (today) {
            Calendar.MONDAY -> alarm1.isMon
            Calendar.TUESDAY -> alarm1.isTue
            Calendar.WEDNESDAY -> alarm1.isWed
            Calendar.THURSDAY -> alarm1.isThu
            Calendar.FRIDAY -> alarm1.isFri
            Calendar.SATURDAY -> alarm1.isSat
            Calendar.SUNDAY -> alarm1.isSun
            else -> false
        }
    }

    private fun startAlarmService(context: Context, alarm1: Alarm) {
        val intentService = Intent(context, AlarmService::class.java)
        val bundle = Bundle()
        bundle.putParcelable(context.getString(R.string.arg_alarm_obj), alarm1)
        intentService.putExtra(context.getString(R.string.arg_alarm_bundle_obj), bundle)
        context.startForegroundService(intentService)
    }
}