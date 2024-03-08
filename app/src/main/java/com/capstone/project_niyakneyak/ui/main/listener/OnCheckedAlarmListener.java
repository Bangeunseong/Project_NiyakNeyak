package com.capstone.project_niyakneyak.ui.main.listener;

import com.capstone.project_niyakneyak.data.alarm_model.Alarm;

public interface OnCheckedAlarmListener {
    void OnItemClicked(Alarm alarm);
    void OnItemClicked(Alarm alarm, boolean isChecked);
}
