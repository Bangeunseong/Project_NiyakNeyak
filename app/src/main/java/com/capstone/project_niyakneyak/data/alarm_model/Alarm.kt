package com.capstone.project_niyakneyak.data.alarm_model

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.widget.Toast
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.alarm.receiver.AlarmReceiver
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.Calendar
import java.util.Locale
@IgnoreExtraProperties
data class Alarm(
    var alarmCode: Int = 0, var hour: Int = 6, var min: Int = 0,
    var isStarted: Boolean = false, var isRecurring: Boolean = false, var isMon: Boolean = false,
    var isTue: Boolean = false, var isWed: Boolean = false, var isThu: Boolean = false,
    var isFri: Boolean = false, var isSat: Boolean = false, var isSun: Boolean = false,
    var title: String? = null, var tone: String? = null, var isVibrate: Boolean = false,
    var medsList: MutableList<Long> = mutableListOf()) : Parcelable {

    constructor(parcel: Parcel) : this() {
        alarmCode = parcel.readInt()
        hour = parcel.readInt()
        min = parcel.readInt()
        isStarted = parcel.readByte().toInt() != 0
        isRecurring = parcel.readByte().toInt() != 0
        isMon = parcel.readByte().toInt() != 0
        isTue = parcel.readByte().toInt() != 0
        isWed = parcel.readByte().toInt() != 0
        isThu = parcel.readByte().toInt() != 0
        isFri = parcel.readByte().toInt() != 0
        isSat = parcel.readByte().toInt() != 0
        isSun = parcel.readByte().toInt() != 0
        title = parcel.readString().toString()
        tone = parcel.readString().toString()
        isVibrate = parcel.readByte().toInt() != 0
        medsList = parcel.readValue(MutableList::class.java.classLoader) as MutableList<Long>
    }

    // Scheduling and Canceling Alarm
    fun scheduleAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val bundle = Bundle()
        bundle.putParcelable(context.getString(R.string.arg_alarm_obj), this)
        intent.putExtra(context.getString(R.string.arg_alarm_bundle_obj), bundle)
        val alarmPendingIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.getBroadcast(
                context,
                alarmCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            ) else PendingIntent.getBroadcast(
                context,
                alarmCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar[Calendar.HOUR_OF_DAY] = hour
        calendar[Calendar.MINUTE] = min
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        if (!isRecurring) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Toast.makeText(
                    context,
                    String.format("Alarm exact for %02d:%02d", hour, min),
                    Toast.LENGTH_SHORT
                ).show()
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        alarmPendingIntent
                    )
                } else alarmManager[AlarmManager.RTC_WAKEUP, calendar.timeInMillis] =
                    alarmPendingIntent
            } else alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                alarmPendingIntent
            )
        } else {
            Toast.makeText(
                context,
                String.format("Alarm recurring for %02d:%02d", hour, min),
                Toast.LENGTH_SHORT
            ).show()
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                alarmPendingIntent
            )
        }
        isStarted = true
    }

    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val alarmPendingIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.getBroadcast(
                context,
                alarmCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            ) else PendingIntent.getBroadcast(
                context,
                alarmCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        isStarted = false
        alarmManager.cancel(alarmPendingIntent)
        Toast.makeText(
            context,
            String.format("Alarm cancelled for %02d:%02d", hour, min),
            Toast.LENGTH_SHORT
        ).show()
    }

    val daysText: String
        get() {
        if (!isRecurring) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()

            return if (hour < calendar[Calendar.HOUR_OF_DAY]) {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                String.format(Locale.KOREAN, "Next Day %02d/%02d", calendar[Calendar.MONTH] + 1, calendar[Calendar.DAY_OF_MONTH])
            } else if (hour == calendar[Calendar.HOUR_OF_DAY]) {
                if (min <= calendar[Calendar.MINUTE]) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                    String.format(Locale.KOREAN, "Next Day %02d/%02d", calendar[Calendar.MONTH] + 1, calendar[Calendar.DAY_OF_MONTH])
                } else String.format(Locale.KOREAN, "Today %02d/%02d", calendar[Calendar.MONTH] + 1, calendar[Calendar.DAY_OF_MONTH])
            } else
                String.format(Locale.KOREAN, "Today %02d/%02d", calendar[Calendar.MONTH] + 1, calendar[Calendar.DAY_OF_MONTH])
        }
        val builder = StringBuilder()
        if (isSun) builder.append("Su ")
        if (isMon) builder.append("Mo ")
        if (isTue) builder.append("Tu ")
        if (isWed) builder.append("We ")
        if (isThu) builder.append("Th ")
        if (isFri) builder.append("Fr ")
        if (isSat) builder.append("Sa")
        return builder.toString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(alarmCode)
        dest.writeInt(hour)
        dest.writeInt(min)
        dest.writeByte((if (isStarted) 1 else 0).toByte())
        dest.writeByte((if (isRecurring) 1 else 0).toByte())
        dest.writeByte((if (isMon) 1 else 0).toByte())
        dest.writeByte((if (isTue) 1 else 0).toByte())
        dest.writeByte((if (isWed) 1 else 0).toByte())
        dest.writeByte((if (isThu) 1 else 0).toByte())
        dest.writeByte((if (isFri) 1 else 0).toByte())
        dest.writeByte((if (isSat) 1 else 0).toByte())
        dest.writeByte((if (isSun) 1 else 0).toByte())
        dest.writeString(title)
        dest.writeString(tone)
        dest.writeByte((if (isVibrate) 1 else 0).toByte())
        dest.writeValue(medsList)
    }

    companion object CREATOR : Parcelable.Creator<Alarm> {
        const val FIELD_ALARM_CODE = "alarmCode"
        const val FIELD_HOUR = "hour"
        const val FIELD_MINUTE = "min"
        const val FIELD_IS_STARTED = "isStarted"
        const val FIELD_IS_RECURRING = "isRecurring"
        const val FIELD_IS_MONDAY = "isMon"
        const val FIELD_IS_TUESDAY = "isTue"
        const val FIELD_IS_WEDNESDAY = "isWed"
        const val FIELD_IS_THURSDAY = "isThu"
        const val FIELD_IS_FRIDAY = "isFri"
        const val FIELD_IS_SATURDAY = "isSat"
        const val FIELD_IS_SUNDAY = "isSun"
        const val FIELD_TITLE = "title"
        const val FIELD_TONE = "tone"
        const val FIELD_IS_VIBRATE = "isVibrate"
        const val FIELD_MEDICATION_LIST = "medsList"

        override fun createFromParcel(parcel: Parcel): Alarm {
            return Alarm(parcel)
        }

        override fun newArray(size: Int): Array<Alarm?> {
            return arrayOfNulls(size)
        }
    }
}