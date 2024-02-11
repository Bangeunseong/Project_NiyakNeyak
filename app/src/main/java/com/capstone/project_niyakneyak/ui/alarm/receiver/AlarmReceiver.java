package com.capstone.project_niyakneyak.ui.alarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.alarm_model.Alarm;
import com.capstone.project_niyakneyak.ui.alarm.service.AlarmService;
import com.capstone.project_niyakneyak.ui.alarm.service.RescheduleAlarmService;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    Alarm alarm;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            String toastText = String.format("Alarm Reboot");
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            startRescheduleAlarmsService(context);
        }
        else {
            Bundle bundle = intent.getBundleExtra(context.getString(R.string.arg_alarm_bundle_obj));
            if (bundle!=null)
                alarm = (Alarm)bundle.getSerializable(context.getString(R.string.arg_alarm_obj));
            String toastText = String.format("Alarm Received");
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            if(alarm != null) {
                if (isAlarmToday(alarm))
                    startAlarmService(context, alarm);
            }
        }
    }

    private boolean isAlarmToday(Alarm alarm1) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int today = calendar.get(Calendar.DAY_OF_WEEK);

        switch(today) {
            case Calendar.MONDAY:
                if (alarm1.isMon())
                    return true;
                return false;
            case Calendar.TUESDAY:
                if (alarm1.isTue())
                    return true;
                return false;
            case Calendar.WEDNESDAY:
                if (alarm1.isWed())
                    return true;
                return false;
            case Calendar.THURSDAY:
                if (alarm1.isThu())
                    return true;
                return false;
            case Calendar.FRIDAY:
                if (alarm1.isFri())
                    return true;
                return false;
            case Calendar.SATURDAY:
                if (alarm1.isSat())
                    return true;
                return false;
            case Calendar.SUNDAY:
                if (alarm1.isSun())
                    return true;
                return false;
        }
        return false;
    }

    private void startAlarmService(Context context, Alarm alarm1) {
        Intent intentService = new Intent(context, AlarmService.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable("AlarmObject",alarm1);
        intentService.putExtra("AlarmBundleObject",bundle);
        context.startForegroundService(intentService);
    }
    private void startRescheduleAlarmsService(Context context) {
        Intent intentService = new Intent(context, RescheduleAlarmService.class);
        context.startForegroundService(intentService);
    }
}
