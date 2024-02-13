package com.capstone.project_niyakneyak.ui.listener;

import com.capstone.project_niyakneyak.data.model.TimeData;

public interface OnChangedTimeListener {
    void onChangedTime(TimeData origin, TimeData changed, int position);
}
