package com.capstone.project_niyakneyak.ui.main.listener;

import com.capstone.project_niyakneyak.data.alarm_model.Alarm;

public interface OnToggleAlarmListener {
    public void onToggle(Alarm alarm);
    public void onDelete(Alarm alarm);
    public void onItemClick(Alarm alarm);
}
