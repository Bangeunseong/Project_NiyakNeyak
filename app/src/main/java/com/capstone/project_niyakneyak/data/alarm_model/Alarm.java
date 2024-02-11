package com.capstone.project_niyakneyak.data.alarm_model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.capstone.project_niyakneyak.ui.alarm.receiver.AlarmReceiver;

import java.io.Serializable;
import java.util.Calendar;

@Entity(tableName = "alarm_table")
public class Alarm implements Serializable {
    // Field
    @PrimaryKey
    @NonNull
    private int alarmCode;
    private int hour, min;
    private boolean started;
    private boolean mon, tue, wed, thu, fri, sat, sun;
    private String title;
    private String tone;
    private boolean vibrate;

    // Constructor
    public Alarm(int alarmCode, int hour, int min, String title, boolean started,
                 boolean mon, boolean tue, boolean wed, boolean thu, boolean fri, boolean sat, boolean sun,
                 String tone, boolean vibrate){
        this.alarmCode = alarmCode;
        this.hour = hour; this.min = min;
        this.started = started;
        this.title = title;
        this.mon = mon; this.tue = tue; this.wed = wed;
        this.thu = thu; this.fri = fri; this.sat = sat; this.sun = sun;
        this.tone = tone; this.vibrate = vibrate;
    }

    // Methods -> Getter and Setter

    // Setter
    public void setAlarmCode(int alarmCode) {this.alarmCode = alarmCode;}
    public void setHour(int hour) {this.hour = hour;}
    public void setMin(int min) {this.min = min;}
    public void setStarted(boolean started) {this.started = started;}

    public void setMon(boolean mon) {this.mon = mon;}
    public void setTue(boolean tue) {this.tue = tue;}
    public void setWed(boolean wed) {this.wed = wed;}
    public void setThu(boolean thu) {this.thu = thu;}
    public void setFri(boolean fri) {this.fri = fri;}
    public void setSat(boolean sat) {this.sat = sat;}
    public void setSun(boolean sun) {this.sun = sun;}

    public void setTitle(String title) {this.title = title;}
    public void setTone(String tone) {this.tone = tone;}
    public void setVibrate(boolean vibrate) {this.vibrate = vibrate;}

    // Getter
    public int getAlarmCode() {return alarmCode;}
    public int getHour() {return hour;}
    public int getMin() {return min;}
    public boolean isStarted() {return started;}

    public boolean isMon() {return mon;}
    public boolean isTue() {return tue;}
    public boolean isWed() {return wed;}
    public boolean isThu() {return thu;}
    public boolean isFri() {return fri;}
    public boolean isSat() {return sat;}
    public boolean isSun() {return sun;}

    public String getTitle() {return title;}
    public String getTone() {return tone;}
    public boolean isVibrate() {return vibrate;}


    // Scheduling and Canceling Alarm
    public void scheduleAlarm(Context context){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("AlarmObject", this);
        intent.putExtra("AlarmBundleObject", bundle);
        PendingIntent alarmPendingIntent = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ?
                PendingIntent.getBroadcast(context, alarmCode, intent, PendingIntent.FLAG_IMMUTABLE) :
                PendingIntent.getBroadcast(context,alarmCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if(calendar.getTimeInMillis() <= System.currentTimeMillis()) return;

        AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), alarmPendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms())
                alarmManager.setAlarmClock(alarmClockInfo, alarmPendingIntent);
            else alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmPendingIntent);
        }
        else alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmPendingIntent);
    }

    public void cancelAlarm(Context context){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmPendingIntent = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ?
                PendingIntent.getBroadcast(context, alarmCode, intent, PendingIntent.FLAG_IMMUTABLE) :
                PendingIntent.getBroadcast(context,alarmCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(alarmPendingIntent);
        this.started = false;
        Toast.makeText(context, String.format("Alarm cancelled for %02d:%02d", hour, min), Toast.LENGTH_SHORT).show();
    }
}
