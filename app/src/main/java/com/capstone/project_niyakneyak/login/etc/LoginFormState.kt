package com.capstone.project_niyakneyak.login.etc

/**
 * Data validation state of the login form.
 */
class LoginFormState {
    var usernameError: Int?
        private set
    var passwordError: Int?
        private set
    var isDataValid: Boolean
        private set

    constructor(usernameError: Int?, passwordError: Int?) {
        this.usernameError = usernameError
        this.passwordError = passwordError
        isDataValid = false
    }

    constructor(isDataValid: Boolean) {
        usernameError = null
        passwordError = null
        this.isDataValid = isDataValid
    }
}