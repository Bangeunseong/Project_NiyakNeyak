package com.capstone.project_niyakneyak.login.etc

/**
 * Authentication result : success (user details) or error message.
 */
class LoginResult {
    var success: LoggedInUserView? = null
        private set
    var error: Int? = null
        private set

    constructor(error: Int?) {
        this.error = error
    }

    constructor(success: LoggedInUserView?) {
        this.success = success
    }
}