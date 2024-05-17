package com.capstone.project_niyakneyak.main.activity

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.capstone.project_niyakneyak.databinding.ActivityAppSettingsBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

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

        auth = FirebaseAuth.getInstance() // Firebase Auth 초기화
        firestore = FirebaseFirestore.getInstance() // Firestore 초기화
        user = auth?.currentUser // 현재 사용자 가져오기

        binding.buttonWithdrawal.setOnClickListener {
            promptForPassword()
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.button4.setOnClickListener {
            user?.let { user ->
                checkSignInProvider(user)
            } ?: run {
                Toast.makeText(this, "User not found", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkSignInProvider(user: FirebaseUser) {
        var isGoogleUser = false
        for (profile in user.providerData) {
            when (profile.providerId) {
                GoogleAuthProvider.PROVIDER_ID -> {
                    isGoogleUser = true
                    break
                }
                EmailAuthProvider.PROVIDER_ID -> {
                    // Continue checking other providers
                }
            }
        }
        if (isGoogleUser) {
            showGoogleSignInErrorDialog()
        } else {
            user.email?.let { email ->
                sendPasswordResetEmail(email)
            } ?: run {
                Toast.makeText(this, "User email not found", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showGoogleSignInErrorDialog() {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage("You cannot reset the password for a Google sign-in account.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun sendPasswordResetEmail(email: String) {
        auth?.sendPasswordResetEmail(email)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Failed to send reset email: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun promptForPassword() {
        AlertDialog.Builder(this)
            .setTitle("회원 탈퇴 확인")
            .setMessage("정말로 탈퇴하시겠습니까? 탈퇴한 계정은 복구할 수 없습니다.")
            .setPositiveButton("계속하기") { dialog, which ->
                showPasswordDialog()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun showPasswordDialog() {
        val passwordInput = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        AlertDialog.Builder(this)
            .setTitle("비밀번호 확인")
            .setMessage("회원 탈퇴를 진행하기 위해 비밀번호를 다시 입력해 주세요.")
            .setView(passwordInput)
            .setPositiveButton("확인") { dialog, which ->
                val password = passwordInput.text.toString()
                attemptToDeleteUser(password)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun attemptToDeleteUser(password: String) {
        user?.email?.let { email ->
            val credential = EmailAuthProvider.getCredential(email, password)
            user?.reauthenticate(credential)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    deleteUserAccount()
                } else {
                    Log.e(TAG, "Reauthentication failed: ", task.exception)
                    promptReauthentication()
                }
            }
        }
    }

    private fun deleteUserAccount() {
        user?.let { user ->
            user.delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "FirebaseAuth user account deleted.")
                    deleteUserFromFirestore(user.uid)
                } else {
                    Log.e(TAG, "Error deleting FirebaseAuth user account", task.exception)
                    promptReauthentication()
                }
            }
        }
    }

    private fun deleteUserFromFirestore(uid: String) {
        firestore.collection("users").document(uid).delete().addOnSuccessListener {
            Log.d(TAG, "Firestore user data deleted")
            setResult(RESULT_OK)
            auth?.signOut()
            finish()
        }.addOnFailureListener { e ->
            Log.e(TAG, "Error deleting Firestore user data", e)
        }
    }

    private fun promptReauthentication() {
        AlertDialog.Builder(this)
            .setTitle("재인증 필요")
            .setMessage("회원 탈퇴를 위해 최근에 로그인해야 합니다. 로그아웃 후 다시 로그인 해주세요.")
            .setPositiveButton("로그아웃") { dialog, which ->
                auth?.signOut()
                finish() // 이걸로 Settingfragment로 이동
            }
            .setNegativeButton("취소", null)
            .show()
    }
}
