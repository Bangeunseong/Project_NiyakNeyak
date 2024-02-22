package com.capstone.project_niyakneyak.ui.alarm.service;

import static com.capstone.project_niyakneyak.App.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.alarm_model.Alarm;
import com.capstone.project_niyakneyak.ui.alarm.activity.RingActivity;

import java.io.IOException;

public class AlarmService extends Service {
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    Alarm alarm;
    Uri ringtone;
    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(true);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        ringtone= RingtoneManager.getActualDefaultRingtoneUri(this.getBaseContext(), RingtoneManager.TYPE_ALARM);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle=intent.getBundleExtra(getString(R.string.arg_alarm_bundle_obj));
        if (bundle!=null)
            alarm =(Alarm)bundle.getParcelable(getString(R.string.arg_alarm_obj));
        Intent notificationIntent = new Intent(this, RingActivity.class);
        notificationIntent.putExtra(getString(R.string.arg_alarm_bundle_obj), bundle);

        PendingIntent pendingIntent = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ?
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE) :
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String alarmTitle = getString(R.string.alarm_title);
        if(alarm != null) {
            alarmTitle = alarm.getTitle();
            try {
                mediaPlayer.setDataSource(this.getBaseContext(), Uri.parse(alarm.getTone()));
                mediaPlayer.prepareAsync();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        else{
            try {
                mediaPlayer.setDataSource(this.getBaseContext(),ringtone);
                mediaPlayer.prepareAsync();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Ring Ring .. Ring Ring")
                .setContentText(alarmTitle)
                .setSmallIcon(R.drawable.ic_alarm_purple)
                .setSound(null)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setFullScreenIntent(pendingIntent,true)
                .build();
        mediaPlayer.setOnPreparedListener(MediaPlayer::start);

        if(alarm.isVibrate()) {
            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
        }
        startForeground(1, notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mediaPlayer.stop();
        vibrator.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}