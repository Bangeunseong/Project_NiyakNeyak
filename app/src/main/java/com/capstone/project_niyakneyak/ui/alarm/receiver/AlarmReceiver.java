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
                alarm = (Alarm)bundle.getParcelable(context.getString(R.string.arg_alarm_obj));
            String toastText = String.format("Alarm Received");
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            if(alarm!=null) {
                if (!alarm.isRecurring()) {
                    startAlarmService(context, alarm);
                } else {
                    if (isAlarmToday(alarm)) {
                        startAlarmService(context, alarm);
                    }
                }
            }
        }
    }

    private boolean isAlarmToday(Alarm alarm1) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int today = calendar.get(Calendar.DAY_OF_WEEK);

        return switch (today) {
            case Calendar.MONDAY -> alarm1.isMon();
            case Calendar.TUESDAY -> alarm1.isTue();
            case Calendar.WEDNESDAY -> alarm1.isWed();
            case Calendar.THURSDAY -> alarm1.isThu();
            case Calendar.FRIDAY -> alarm1.isFri();
            case Calendar.SATURDAY -> alarm1.isSat();
            case Calendar.SUNDAY -> alarm1.isSun();
            default -> false;
        };
    }

    private void startAlarmService(Context context, Alarm alarm1) {
        Intent intentService = new Intent(context, AlarmService.class);
        Bundle bundle=new Bundle();
        bundle.putParcelable(context.getString(R.string.arg_alarm_obj),alarm1);
        intentService.putExtra(context.getString(R.string.arg_alarm_bundle_obj),bundle);
        context.startForegroundService(intentService);
    }
    private void startRescheduleAlarmsService(Context context) {
        Intent intentService = new Intent(context, RescheduleAlarmService.class);
        context.startForegroundService(intentService);
    }
}
