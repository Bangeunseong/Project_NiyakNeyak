package com.capstone.project_niyakneyak.main.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.capstone.project_niyakneyak.databinding.ActivityAppSettingsBinding
import com.capstone.project_niyakneyak.login.activity.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class AppSettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppSettingsBinding
    private var auth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private lateinit var firestore: FirebaseFirestore

    companion object {
        const val TAG = "AppSettingActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAppSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth // Firebase Auth 초기화
        firestore = Firebase.firestore // Firestore 초기화
        user = auth?.currentUser // 현재 사용자 가져오기

        binding.buttonWithdrawal.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("회원 탈퇴")
                .setMessage("정말로 탈퇴하시겠습니까?")
                .setPositiveButton("예") { dialog, which ->
                    deleteUserAccount()
                }
                .setNegativeButton("아니오", null)
                .show()

        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun deleteUserAccount() {
        user?.let { user ->
            // Firestore에서 사용자 데이터 삭제
            firestore.collection("users").document(user.uid).delete().addOnSuccessListener {
                Log.d(TAG, "Firestore user data deleted")
                // 인증 정보에서 사용자 삭제
                deleteUserAuth(user)
            }.addOnFailureListener { e ->
                Log.w(TAG, "Error deleting Firestore user data", e)
            }
        }
    }

    private fun deleteUserAuth(user: FirebaseUser) {
        user.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "FirebaseAuth user account deleted.")
                auth?.signOut() // 사용자 삭제 후 로그아웃
                // 회원 탈퇴 후 처리, 예를 들어 로그인 화면으로 이동
                val intent = Intent(this, LoginActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
                finish()
            } else {
                Log.w(TAG, "Error deleting FirebaseAuth user account", task.exception)
            }
        }
    }
}
