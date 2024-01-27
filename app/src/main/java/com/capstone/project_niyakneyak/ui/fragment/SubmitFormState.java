package com.capstone.project_niyakneyak.ui.fragment;

import androidx.annotation.Nullable;

public class SubmitFormState {
    @Nullable
    private Integer meds_nameError;
    private boolean isDataValid;

    SubmitFormState(@Nullable Integer meds_nameError){
        this.meds_nameError = meds_nameError;
        isDataValid = false;
    }
    SubmitFormState(boolean isDataValid){
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getMedsNameError() {
        return meds_nameError;
    }
    boolean isDataValid() {
        return isDataValid;
    }
}
