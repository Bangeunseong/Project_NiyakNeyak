package com.capstone.project_niyakneyak.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.capstone.project_niyakneyak.alarm.service.RescheduleAlarmService


class RescheduleAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action){
            Intent.ACTION_LOCKED_BOOT_COMPLETED -> startRescheduleAlarmsService(context)
        }
    }
    private fun startRescheduleAlarmsService(context: Context) {
        val intentService = Intent(context, RescheduleAlarmService::class.java)
        context.startForegroundService(intentService)
    }
}