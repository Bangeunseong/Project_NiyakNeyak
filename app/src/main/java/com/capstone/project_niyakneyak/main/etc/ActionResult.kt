package com.capstone.project_niyakneyak.main.etc

class ActionResult {
    var alarmDateView: AlarmDataView? = null
        private set
    var success: DataView? = null
        private set
    var error: Int? = null
        private set

    constructor(error: Int?) {
        this.error = error
    }

    constructor(success: DataView?) {
        this.success = success
    }

    constructor(alarmDataView: AlarmDataView?) {
        alarmDateView = alarmDataView
    }
}