package com.capstone.project_niyakneyak.alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.capstone.project_niyakneyak.R;

public class AlarmReceiver extends BroadcastReceiver {
    private NotificationManager manager;
    private NotificationCompat.Builder builder;
    private static final String CHANNEL_ID = "Alarm_Channel";
    private static final String CHANNEL_NAME="Alarm_Channel_1";

    @Override
    public void onReceive(Context context, Intent intent) {
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT));

        builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        Intent alarm_intent = new Intent(context, AlarmService.class);
        int requestCOde = intent.getIntExtra("alarm_rqCode", 0);
        String title = intent.getStringExtra("alarm_title");

        PendingIntent pendingIntent = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ?
                PendingIntent.getActivity(context,requestCOde,alarm_intent,PendingIntent.FLAG_IMMUTABLE) :
                PendingIntent.getActivity(context, requestCOde,alarm_intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = builder.setContentTitle(title)
                .setContentText("SCHEDULE MANAGER")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        manager.notify(1, notification);
    }
}
