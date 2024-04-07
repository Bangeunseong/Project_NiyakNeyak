package com.capstone.project_niyakneyak.data.user_model

import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * 사용자 계정 정보 모델 클래스
 */
@IgnoreExtraProperties
data class UserAccount(
    var idToken: String? = null,
    var emailId: String? = null,
    var password: String? = null,
    var name: String? = null,
    var birth: String? = null,
    var phoneNum: String? = null){

    companion object{
        const val REPRESENT_KEY = "USER"
        const val COLLECTION_ID = "users"
        const val ID_TOKEN = "idToken"
        const val EMAIL_ID = "emailId"
        const val PASSWORD = "password"
        const val NAME = "name"
        const val PHONE_NUMBER = "phoneNum"
    }
}