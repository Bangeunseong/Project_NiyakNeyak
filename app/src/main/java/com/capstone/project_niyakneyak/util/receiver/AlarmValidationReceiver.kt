package com.capstone.project_niyakneyak.util.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.alarm_valid_model.AlarmV
import com.capstone.project_niyakneyak.util.service.AlarmValidationService

class AlarmValidationReceiver: BroadcastReceiver() {
    var alarm: AlarmV? = null
    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.getBundleExtra(context.getString(R.string.arg_alarm_bundle_obj)) ?: return

        alarm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(context.getString(R.string.arg_alarm_obj), AlarmV::class.java)
        } else {
            @Suppress("DEPRECATION")
            bundle.getParcelable(context.getString(R.string.arg_alarm_obj))
        }
        Log.w("AlarmValidationReceiver", "Alarm Received")
        if (alarm != null) {
            startAlarmValidationService(context, alarm!!)
        }
    }

    private fun startAlarmValidationService(context: Context, alarm1: AlarmV) {
        val intentService = Intent(context, AlarmValidationService::class.java)
        val bundle = Bundle()
        bundle.putParcelable(context.getString(R.string.arg_alarm_obj), alarm1)
        intentService.putExtra(context.getString(R.string.arg_alarm_bundle_obj), bundle)
        context.startForegroundService(intentService)
    }
}