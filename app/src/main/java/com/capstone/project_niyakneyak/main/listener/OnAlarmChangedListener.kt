package com.capstone.project_niyakneyak.main.listener

import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.google.firebase.firestore.DocumentSnapshot

interface OnAlarmChangedListener {
    fun onDelete(snapshot: DocumentSnapshot)
    fun onItemClick(snapshot: DocumentSnapshot)
}