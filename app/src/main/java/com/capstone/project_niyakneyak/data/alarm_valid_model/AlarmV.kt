package com.capstone.project_niyakneyak.data.alarm_valid_model

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.util.receiver.AlarmValidationReceiver
import java.util.Calendar

data class AlarmV(
    var alarmCode: Int = 0,
    var hour: Int = 0,
    var min: Int = 0,
    var isStarted: Boolean = false): Parcelable {

    fun scheduleAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmValidationReceiver::class.java)
        val bundle = Bundle()
        bundle.putParcelable(context.getString(R.string.arg_alarm_obj), this)
        intent.putExtra(context.getString(R.string.arg_alarm_bundle_obj), bundle)
        val alarmPendingIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.getBroadcast(context, alarmCode, intent, PendingIntent.FLAG_IMMUTABLE)
            else PendingIntent.getBroadcast(context, alarmCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar[Calendar.HOUR_OF_DAY] = hour
        calendar[Calendar.MINUTE] = min
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, alarmPendingIntent)
        isStarted = true
    }

    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmValidationReceiver::class.java)
        val alarmPendingIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.getBroadcast(context, alarmCode, intent, PendingIntent.FLAG_IMMUTABLE)
            else PendingIntent.getBroadcast(context, alarmCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        isStarted = false
        alarmManager.cancel(alarmPendingIntent)
    }

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(alarmCode)
        parcel.writeInt(hour)
        parcel.writeInt(min)
        parcel.writeByte(if (isStarted) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AlarmV> {
        override fun createFromParcel(parcel: Parcel): AlarmV {
            return AlarmV(parcel)
        }

        override fun newArray(size: Int): Array<AlarmV?> {
            return arrayOfNulls(size)
        }
    }
}