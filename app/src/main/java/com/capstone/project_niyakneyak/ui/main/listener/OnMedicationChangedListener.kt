package com.capstone.project_niyakneyak.ui.main.listener

import com.google.firebase.firestore.DocumentSnapshot

interface OnMedicationChangedListener {
    fun onModifyBtnClicked(target: DocumentSnapshot)
    fun onDeleteBtnClicked(target: DocumentSnapshot)
}