package com.capstone.project_niyakneyak.ui.main.listener

import com.capstone.project_niyakneyak.data.patient_model.MedsData

interface OnChangedDataListener {
    fun onChangedData(origin: MedsData, changed: MedsData)
}