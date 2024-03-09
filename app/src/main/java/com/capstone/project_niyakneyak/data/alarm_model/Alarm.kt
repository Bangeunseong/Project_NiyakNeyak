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
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.ui.alarm.receiver.AlarmReceiver
import java.util.Calendar
import java.util.Locale

@Entity(tableName = "alarm_table")
class Alarm : Parcelable {
    // Methods -> Getter and Setter
    // Setter
    // Getter
    // Field
    @JvmField
    @PrimaryKey
    var alarmCode: Int
    @JvmField
    var hour: Int
    @JvmField
    var min: Int
    var isStarted: Boolean
    var isRecurring: Boolean
    var isMon: Boolean
    var isTue: Boolean
    var isWed: Boolean
    var isThu: Boolean
    var isFri: Boolean
    var isSat: Boolean
    var isSun: Boolean
    @JvmField
    var title: String?
    @JvmField
    var tone: String?
    var isVibrate: Boolean

    // Constructor
    constructor(
        alarmCode: Int,
        hour: Int,
        min: Int,
        title: String?,
        started: Boolean,
        recurring: Boolean,
        mon: Boolean,
        tue: Boolean,
        wed: Boolean,
        thu: Boolean,
        fri: Boolean,
        sat: Boolean,
        sun: Boolean,
        tone: String?,
        vibrate: Boolean
    ) {
        this.alarmCode = alarmCode
        this.hour = hour
        this.min = min
        isStarted = started
        isRecurring = recurring
        this.title = title
        isMon = mon
        isTue = tue
        isWed = wed
        isThu = thu
        isFri = fri
        isSat = sat
        isSun = sun
        this.tone = tone
        isVibrate = vibrate
    }

    protected constructor(`in`: Parcel) {
        alarmCode = `in`.readInt()
        hour = `in`.readInt()
        min = `in`.readInt()
        isStarted = `in`.readByte().toInt() != 0
        isRecurring = `in`.readByte().toInt() != 0
        isMon = `in`.readByte().toInt() != 0
        isTue = `in`.readByte().toInt() != 0
        isWed = `in`.readByte().toInt() != 0
        isThu = `in`.readByte().toInt() != 0
        isFri = `in`.readByte().toInt() != 0
        isSat = `in`.readByte().toInt() != 0
        isSun = `in`.readByte().toInt() != 0
        title = `in`.readString()
        tone = `in`.readString()
        isVibrate = `in`.readByte().toInt() != 0
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

    val daysText: String? get() {
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
    }

    companion object CREATOR : Parcelable.Creator<Alarm> {
        override fun createFromParcel(parcel: Parcel): Alarm {
            return Alarm(parcel)
        }

        override fun newArray(size: Int): Array<Alarm?> {
            return arrayOfNulls(size)
        }
    }


}