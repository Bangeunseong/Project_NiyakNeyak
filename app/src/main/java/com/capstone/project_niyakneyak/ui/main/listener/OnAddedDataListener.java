package com.capstone.project_niyakneyak.ui.main.listener;

import com.capstone.project_niyakneyak.data.patient_model.MedsData;

import java.io.Serializable;

public interface OnAddedDataListener extends Serializable {
    void onAddedData(MedsData target);
}
