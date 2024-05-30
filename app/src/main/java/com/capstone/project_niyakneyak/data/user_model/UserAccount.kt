package com.capstone.project_niyakneyak.data.user_model

import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * 사용자 계정 정보 모델 클래스
 */
// 파이어베이스에서는 빈 생성자를 만들어줘야 한다. 모델클래스를 통해서 가져오는데 빈 생성자 반드시 필요하다.
@IgnoreExtraProperties
data class UserAccount(
    var idToken: String? = null,
    var emailId: String? = null,
    var password: String? = null,
    var name: String? = null,
    var birth: String? = null,
    var gender: String? = null,
    var phoneNum: String? = null,
    var age: Int? = null,
    var profilePic: String? = null,
    var address: String? = null
)
{
    companion object{
        const val REPRESENT_KEY = "USER"
        const val COLLECTION_ID = "users"
        const val ID_TOKEN = "idToken"
        const val EMAIL_ID = "emailId"
        const val PASSWORD = "password"
        const val NAME = "name"
        const val PHONE_NUMBER = "phoneNum"
        const val BIRTH = "birth"
        const val PROFILE = "profilePic"
    }
}