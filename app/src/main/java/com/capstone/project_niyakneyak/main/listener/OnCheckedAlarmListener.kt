package com.capstone.project_niyakneyak.main.listener

import com.capstone.project_niyakneyak.data.alarm_model.Alarm

interface OnCheckedAlarmListener {
    fun onItemClicked(alarm: Alarm)
    fun onItemClicked(medsID: Long, alarm: Alarm, isChecked: Boolean)
}