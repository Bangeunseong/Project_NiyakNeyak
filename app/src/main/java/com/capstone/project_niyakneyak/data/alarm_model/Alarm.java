package com.capstone.project_niyakneyak.data.alarm_model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.ui.alarm.receiver.AlarmReceiver;

import java.util.Calendar;

@Entity(tableName = "alarm_table")
public class Alarm implements Parcelable {
    // Field
    @PrimaryKey
    @NonNull
    private int alarmCode;
    private int hour, min;
    private boolean started;
    private boolean recurring;
    private boolean mon, tue, wed, thu, fri, sat, sun;
    private String title;
    private String tone;
    private boolean vibrate;

    // Constructor
    public Alarm(int alarmCode, int hour, int min, String title, boolean started, boolean recurring,
                 boolean mon, boolean tue, boolean wed, boolean thu, boolean fri, boolean sat, boolean sun,
                 String tone, boolean vibrate){
        this.alarmCode = alarmCode;
        this.hour = hour; this.min = min;
        this.started = started;
        this.recurring = recurring;
        this.title = title;
        this.mon = mon; this.tue = tue; this.wed = wed;
        this.thu = thu; this.fri = fri; this.sat = sat; this.sun = sun;
        this.tone = tone; this.vibrate = vibrate;
    }

    protected Alarm(Parcel in) {
        alarmCode = in.readInt();
        hour = in.readInt();
        min = in.readInt();
        started = in.readByte() != 0;
        recurring = in.readByte() != 0;
        mon = in.readByte() != 0;
        tue = in.readByte() != 0;
        wed = in.readByte() != 0;
        thu = in.readByte() != 0;
        fri = in.readByte() != 0;
        sat = in.readByte() != 0;
        sun = in.readByte() != 0;
        title = in.readString();
        tone = in.readString();
        vibrate = in.readByte() != 0;
    }

    public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
        @Override
        public Alarm createFromParcel(Parcel in) {
            return new Alarm(in);
        }

        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };

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
    public void setRecurring(boolean recurring) {this.recurring = recurring;}

    // Getter
    public int getAlarmCode() {return alarmCode;}
    public int getHour() {return hour;}
    public int getMin() {return min;}
    public boolean isStarted() {return started;}
    public boolean isRecurring() {return recurring;}

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
        bundle.putParcelable(context.getString(R.string.arg_alarm_obj), this);
        intent.putExtra(context.getString(R.string.arg_alarm_bundle_obj), bundle);
        PendingIntent alarmPendingIntent = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ?
                PendingIntent.getBroadcast(context, alarmCode, intent, PendingIntent.FLAG_IMMUTABLE) :
                PendingIntent.getBroadcast(context,alarmCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if(calendar.getTimeInMillis() <= System.currentTimeMillis()){
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        if(!recurring){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Toast.makeText(context, String.format("Alarm exact for %02d:%02d", hour, min), Toast.LENGTH_SHORT).show();
                if (alarmManager.canScheduleExactAlarms()){
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmPendingIntent);
                }
                else alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmPendingIntent);
            }
            else alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmPendingIntent);
        } else{
            Toast.makeText(context, String.format("Alarm recurring for %02d:%02d", hour, min), Toast.LENGTH_SHORT).show();
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmPendingIntent);
        }
        started = true;
    }

    public void cancelAlarm(Context context){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmPendingIntent = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ?
                PendingIntent.getBroadcast(context, alarmCode, intent, PendingIntent.FLAG_IMMUTABLE) :
                PendingIntent.getBroadcast(context,alarmCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        this.started = false;
        alarmManager.cancel(alarmPendingIntent);
        Toast.makeText(context, String.format("Alarm cancelled for %02d:%02d", hour, min), Toast.LENGTH_SHORT).show();
    }

    public String getRecurringDaysText() {
        if (!recurring) {return null;}

        StringBuilder builder = new StringBuilder();
        if (sun) builder.append("Su ");
        if (mon) builder.append("Mo ");
        if (tue) builder.append("Tu ");
        if (wed) builder.append("We ");
        if (thu) builder.append("Th ");
        if (fri) builder.append("Fr ");
        if (sat) builder.append("Sa");

        return builder.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(alarmCode);
        dest.writeInt(hour);
        dest.writeInt(min);
        dest.writeByte((byte) (started ? 1 : 0));
        dest.writeByte((byte) (recurring ? 1 : 0));
        dest.writeByte((byte) (mon ? 1 : 0));
        dest.writeByte((byte) (tue ? 1 : 0));
        dest.writeByte((byte) (wed ? 1 : 0));
        dest.writeByte((byte) (thu ? 1 : 0));
        dest.writeByte((byte) (fri ? 1 : 0));
        dest.writeByte((byte) (sat ? 1 : 0));
        dest.writeByte((byte) (sun ? 1 : 0));
        dest.writeString(title);
        dest.writeString(tone);
        dest.writeByte((byte) (vibrate ? 1 : 0));
    }
}
