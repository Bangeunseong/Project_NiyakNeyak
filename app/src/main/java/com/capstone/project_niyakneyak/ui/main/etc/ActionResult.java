package com.capstone.project_niyakneyak.ui.main.etc;

import androidx.annotation.Nullable;

public class ActionResult {
    @Nullable
    private AlarmDataView alarmDataView;
    @Nullable
    private DataView success;
    @Nullable
    private Integer error;

    public ActionResult(@Nullable Integer error){this.error = error;}
    public ActionResult(@Nullable DataView success){this.success = success;}
    public ActionResult(@Nullable AlarmDataView alarmDataView){this.alarmDataView = alarmDataView;}

    @Nullable
    public AlarmDataView getAlarmDateView(){return alarmDataView;}
    @Nullable
    public DataView getSuccess(){return success;}
    @Nullable
    public Integer getError(){return error;}
}
