package com.capstone.project_niyakneyak.ui.main.listener

import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.google.firebase.firestore.DocumentSnapshot

interface OnAlarmChangedListener {
    fun onToggle(snapshot: DocumentSnapshot, alarm: Alarm)
    fun onDelete(snapshot: DocumentSnapshot, alarm: Alarm)
    fun onItemClick(snapshot: DocumentSnapshot, alarm: Alarm)
}