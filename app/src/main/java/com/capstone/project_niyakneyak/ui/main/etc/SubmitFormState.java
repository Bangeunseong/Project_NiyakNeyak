package com.capstone.project_niyakneyak.ui.main.etc;

import androidx.annotation.Nullable;

public class SubmitFormState {
    @Nullable
    private Integer meds_nameError;
    private boolean isDataValid;

    public SubmitFormState(@Nullable Integer meds_nameError){
        this.meds_nameError = meds_nameError;
        isDataValid = false;
    }
    public SubmitFormState(boolean isDataValid){
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getMedsNameError() {
        return meds_nameError;
    }
    public boolean isDataValid() {
        return isDataValid;
    }
}
