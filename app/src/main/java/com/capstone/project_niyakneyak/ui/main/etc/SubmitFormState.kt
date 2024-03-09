package com.capstone.project_niyakneyak.ui.main.etc

class SubmitFormState {
    var medsNameError: Int? = null
        private set
    var isDataValid: Boolean
        private set

    constructor(medsNameError: Int?) {
        this.medsNameError = medsNameError
        isDataValid = false
    }

    constructor(isDataValid: Boolean) {
        this.isDataValid = isDataValid
    }
}