package com.capstone.project_niyakneyak.main.listener

import com.capstone.project_niyakneyak.data.medication_model.MedsData

interface OnCheckedMedicationListener {
    fun onItemClicked(data: MedsData)
}