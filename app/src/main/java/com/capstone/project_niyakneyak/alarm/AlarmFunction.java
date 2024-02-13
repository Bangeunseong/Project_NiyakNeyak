package com.capstone.project_niyakneyak.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.lifecycle.LifecycleCoroutineScope;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmFunction {
    private Context context;
    private PendingIntent pendingIntent;

    public AlarmFunction(Context context){this.context = context;}

    public void callAlarm(String time, int alarm_code, String content){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent receiverIntent = new Intent(context, AlarmReceiver.class);
        receiverIntent.putExtra("alarm_rqCode",alarm_code);
        receiverIntent.putExtra("content",content);

        pendingIntent = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ?
                PendingIntent.getBroadcast(context, alarm_code, receiverIntent,PendingIntent.FLAG_IMMUTABLE) :
                PendingIntent.getBroadcast(context, alarm_code, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        DateFormat formatter = new SimpleDateFormat("HH:mm");
        Date alarm_time = null;
        try {alarm_time = formatter.parse(time);}
        catch (ParseException e) {Log.d("AlarmFunction","Parsing Failed!");}

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(alarm_time);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void cancelAlarm(int alarm_code){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent receiverIntent = new Intent(context, AlarmReceiver.class);

        pendingIntent = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ?
                PendingIntent.getBroadcast(context, alarm_code, receiverIntent,PendingIntent.FLAG_IMMUTABLE) :
                PendingIntent.getBroadcast(context, alarm_code, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
    }
}
