package com.capstone.project_niyakneyak.ui.main.listener;

import com.capstone.project_niyakneyak.data.patient_model.MedsData;

import java.io.Serializable;

public interface OnChangedDataListener extends Serializable {
    void onChangedData(MedsData origin, MedsData changed);
}
