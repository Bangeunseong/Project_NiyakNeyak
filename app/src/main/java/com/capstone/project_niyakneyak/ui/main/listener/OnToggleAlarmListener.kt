package com.capstone.project_niyakneyak.ui.main.listener

import com.capstone.project_niyakneyak.data.alarm_model.Alarm

interface OnToggleAlarmListener {
    fun onToggle(alarm: Alarm)
    fun onDelete(alarm: Alarm)
    fun onItemClick(alarm: Alarm)
}