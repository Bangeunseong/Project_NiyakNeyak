package com.capstone.project_niyakneyak.login.activity

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.databinding.ActivityGoogleRegisterBinding
import com.capstone.project_niyakneyak.databinding.ActivityRegisterBinding
import com.capstone.project_niyakneyak.main.activity.AppSettingActivity.Companion.TAG
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class GoogleRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGoogleRegisterBinding
    private lateinit var firestore: FirebaseFirestore
    private val uiScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoogleRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = Firebase.firestore

        val userId = intent.getStringExtra("uid") ?: ""
        val userEmail = intent.getStringExtra("email") ?: ""
        val userName = intent.getStringExtra("name") ?: ""

        // TextView로 사용자 이메일과 이름 표시
        binding.tvEmail.text = userEmail
        binding.tvName.text = userName

        binding.tvBirth.setOnClickListener {
            showDatePickerDialog()
        }

        binding.btnRegister.setOnClickListener {
            val selectedGenderId = binding.genderRadioGroup.checkedRadioButtonId
            val selectedGender = if (selectedGenderId != -1) {
                findViewById<RadioButton>(selectedGenderId).text.toString()
            } else {
                "성별 선택 안됨"
            }

            val birthDate = binding.tvBirth.text.toString()
            val userAccount = UserAccount(
                idToken = userId,
                emailId = userEmail,
                name = userName,
                birth = birthDate,
                gender = selectedGender,
                phoneNum = binding.etPhoneNum.text.toString(),
                age = calculateAge(birthDate)
            )

            firestore.collection(UserAccount.COLLECTION_ID).document(userId).set(userAccount)
                .addOnSuccessListener {
                    setResult(RESULT_OK)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "회원가입에 실패하셨습니다: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    Log.w(TAG, "Error Occurred: $e")
                }
        }
    }

    private fun showDatePickerDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val dateStr = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            binding.tvBirth.text = dateStr
            updateAge(dateStr)
        }, year, month, day).show()
    }

    private fun updateAge(birthDate: String) {
        uiScope.launch {
            val age = withContext(Dispatchers.Default) {
                calculateAge(birthDate)
            }
            binding.tvAge.text = "만 $age 세"
        }
    }

    private fun calculateAge(birthDateString: String): Int {
        val parts = birthDateString.split("-")
        if (parts.size < 3) return 0

        val birthYear = parts[0].toInt()
        val birthMonth = parts[1].toInt()
        val birthDay = parts[2].toInt()

        val today = Calendar.getInstance()
        val currentYear = today.get(Calendar.YEAR)
        val currentMonth = today.get(Calendar.MONTH) + 1
        val currentDay = today.get(Calendar.DAY_OF_MONTH)

        var age = currentYear - birthYear
        if (birthMonth > currentMonth || (birthMonth == currentMonth && birthDay > currentDay)) {
            age -= 1
        }

        return age
    }

    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }
}
