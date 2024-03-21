package com.capstone.project_niyakneyak.login.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.databinding.ActivityRegisterBinding
import com.capstone.project_niyakneyak.main.activity.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    companion object{
        private const val TAG = "REGISTER_ACTIVITY"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // activity_register.xml을 바인딩합니다.
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mFirebaseAuth = Firebase.auth
        firestore = Firebase.firestore

        binding.btnRegister.setOnClickListener {
            val strEmail = binding.etEmail.text ?: null
            val strPwd = binding.etPassword.text ?: null

            if(strEmail != null && strPwd != null){
                mFirebaseAuth.createUserWithEmailAndPassword(strEmail.toString(), strPwd.toString())
                    .addOnSuccessListener(this@RegisterActivity) {
                        // 인증객체에서 현재의 유저를 가져옴
                        val firebaseUser = mFirebaseAuth.currentUser
                        val account = UserAccount()
                        account.idToken = firebaseUser?.uid
                        account.emailId = firebaseUser?.email
                        account.password = strPwd.toString()
                        account.name = binding.etName.text.toString()
                        account.phoneNum = binding.etPhoneNum.text.toString()

                        // 가입이 이루어졌을 때, 가입 정보를 데이터베이스에 저장
                        firestore.collection("Users").document(account.idToken).set(account)
                            .addOnSuccessListener {
                                Toast.makeText(this@RegisterActivity, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@RegisterActivity, "회원가입에 실패하셨습니다.", Toast.LENGTH_SHORT).show()
                                Log.w(TAG, "Terrible Error Occurred!: $it")
                            }



                    }.addOnFailureListener {
                        Toast.makeText(this@RegisterActivity, "회원가입에 실패하셨습니다.", Toast.LENGTH_SHORT).show()
                        Log.w(TAG, "Terrible Error Occurred!: $it")
                    }
            }
        }
    }
}