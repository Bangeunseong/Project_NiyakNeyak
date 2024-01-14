package com.capstone.project_niyakneyak.ui.main;

import androidx.annotation.Nullable;

public class ActionResult {
    @Nullable
    private MedsDataView success;
    @Nullable
    private Integer error;

    ActionResult(@Nullable Integer error){this.error = error;}
    ActionResult(@Nullable MedsDataView success){this.success = success;}

    @Nullable
    MedsDataView getSuccess(){return success;}
    @Nullable
    Integer getError(){return error;}
}
