package com.capstone.project_niyakneyak.ui.main.listener;

import com.capstone.project_niyakneyak.data.alarm_model.Alarm;

public interface OnToggleAlarmListener {
    void onToggle(Alarm alarm);
    void onDelete(Alarm alarm);
    void onItemClick(Alarm alarm);
}
