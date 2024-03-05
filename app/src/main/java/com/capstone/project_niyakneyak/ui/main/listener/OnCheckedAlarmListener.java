package com.capstone.project_niyakneyak.ui.main.listener;

import com.capstone.project_niyakneyak.data.alarm_model.Alarm;
import com.capstone.project_niyakneyak.data.patient_model.MedsData;

public interface OnCheckedAlarmListener {
    void OnItemClicked(Alarm alarm);
    void OnItemClicked(long medsID, Alarm alarm, boolean isChecked);
}
