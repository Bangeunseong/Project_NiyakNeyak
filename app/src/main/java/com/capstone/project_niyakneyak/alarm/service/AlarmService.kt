package com.capstone.project_niyakneyak.alarm.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.capstone.project_niyakneyak.App
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.alarm.activity.RingActivity
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import java.io.IOException

class AlarmService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    var alarm: Alarm? = null
    var ringtone: Uri? = null
    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.isLooping = true
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
        ringtone = RingtoneManager.getActualDefaultRingtoneUri(
            this.baseContext,
            RingtoneManager.TYPE_ALARM
        )
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val bundle = intent.getBundleExtra(getString(R.string.arg_alarm_bundle_obj))
        if(bundle != null){
            alarm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(getString(R.string.arg_alarm_obj), Alarm::class.java)
            } else {
                @Suppress("DEPRECATION")
                bundle.getParcelable(getString(R.string.arg_alarm_obj))
            }
        }
        val notificationIntent = Intent(this, RingActivity::class.java)
        notificationIntent.putExtra(getString(R.string.arg_alarm_bundle_obj), bundle)
        val pendingIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            ) else PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        var alarmTitle: String? = getString(R.string.alarm_title)
        if (alarm != null) {
            alarmTitle = alarm!!.title
            try {
                mediaPlayer!!.setDataSource(this.baseContext, Uri.parse(alarm!!.tone))
                mediaPlayer!!.prepareAsync()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        } else {
            try {
                mediaPlayer!!.setDataSource(this.baseContext, ringtone!!)
                mediaPlayer!!.prepareAsync()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
        val notification = NotificationCompat.Builder(this, App.CHANNEL_ID)
            .setContentTitle("Ring Ring .. Ring Ring")
            .setContentText(alarmTitle)
            .setSmallIcon(R.drawable.ic_alarm_purple)
            .setSound(null)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setFullScreenIntent(pendingIntent, true)
            .build()
        mediaPlayer!!.setOnPreparedListener { obj: MediaPlayer -> obj.start() }
        if (alarm!!.isVibrate) {
            vibrator!!.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
        }
        startForeground(1, notification)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer!!.stop()
        vibrator!!.cancel()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}