package com.capstone.project_niyakneyak.main.listener

import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.data.medication_model.MedicineData

interface OnCheckedChecklistListener {
    fun onItemClicked(data: MedicineData, alarm: Alarm)
}