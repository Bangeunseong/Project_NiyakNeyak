package com.capstone.project_niyakneyak.main.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.capstone.project_niyakneyak.databinding.ActivityAppSettingsBinding
import com.capstone.project_niyakneyak.login.activity.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AppSettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppSettingsBinding
    private var auth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private lateinit var firestore: FirebaseFirestore // Firestore 인스턴스

    companion object {
        const val TAG = "AppSettingActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAppSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance() // Firebase Auth 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance() // Firestore 인스턴스 초기화
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