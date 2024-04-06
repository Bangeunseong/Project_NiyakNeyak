package com.capstone.project_niyakneyak.main.listener

import com.capstone.project_niyakneyak.data.medication_model.MedicineData

interface OnCheckedMedicationListener {
    fun onItemClicked(data: MedicineData)
}