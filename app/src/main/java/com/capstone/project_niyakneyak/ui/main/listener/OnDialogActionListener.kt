package com.capstone.project_niyakneyak.ui.main.listener

import com.capstone.project_niyakneyak.data.patient_model.MedsData

interface OnDialogActionListener {
    fun onAddedMedicationData(data: MedsData)
    fun onModifiedMedicationData(snapshotID: String, changed: MedsData)
}