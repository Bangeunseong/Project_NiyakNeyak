package com.capstone.project_niyakneyak.ui.main;

import androidx.annotation.Nullable;

public class ActionResult {
    @Nullable
    private DataView success;
    @Nullable
    private Integer error;

    public ActionResult(@Nullable Integer error){this.error = error;}
    public ActionResult(@Nullable DataView success){this.success = success;}

    @Nullable
    public DataView getSuccess(){return success;}
    @Nullable
    public Integer getError(){return error;}
}
