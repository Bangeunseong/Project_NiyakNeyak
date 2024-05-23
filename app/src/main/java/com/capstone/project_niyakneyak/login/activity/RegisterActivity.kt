package com.capstone.project_niyakneyak.login.activity

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.databinding.ActivityRegisterBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.Calendar
import kotlinx.coroutines.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val uiScope = CoroutineScope(Dispatchers.Main)

    companion object {
        private const val TAG = "REGISTER_ACTIVITY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth and Firestore
        auth = Firebase.auth
        firestore = Firebase.firestore

        // Set up click listener for the birth date TextView
        binding.tvBirth.setOnClickListener {
            showDatePickerDialog()
        }

        binding.btnRegister.setOnClickListener {
            val strEmail = binding.etEmail.text.toString().trim()
            val strPwd = binding.etPassword.text.toString().trim()

            if (strEmail.isNotEmpty() && strPwd.isNotEmpty()) {
                val selectedGenderId = binding.genderRadioGroup.checkedRadioButtonId

                val selectedGender = if (selectedGenderId != -1) {
                    findViewById<RadioButton>(selectedGenderId).text.toString()
                } else {
                    "성별 선택 안됨"
                }

                // The birthDate value is directly taken from the TextView
                val birthDate = binding.tvBirth.text.toString()

                auth.createUserWithEmailAndPassword(strEmail, strPwd)
                    .addOnSuccessListener(this@RegisterActivity) {
                        val firebaseUser = auth.currentUser
                        val account = UserAccount(
                            firebaseUser?.uid,
                            firebaseUser?.email,
                            strPwd,
                            binding.etName.text.toString(),
                            birthDate,
                            selectedGender,
                            binding.etPhoneNum.text.toString(),
                            age = calculateAge(birthDate)
                        )

                        firestore.collection(UserAccount.COLLECTION_ID).document(account.idToken!!).set(account)
                            .addOnSuccessListener {
                                setResult(RESULT_OK)
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@RegisterActivity, "회원가입에 실패하셨습니다.", Toast.LENGTH_SHORT).show()
                                Log.w(TAG, "Terrible Error Occurred!: $it")
                            }
                    }.addOnFailureListener {
                        Toast.makeText(this@RegisterActivity, "회원가입에 실패하셨습니다.", Toast.LENGTH_SHORT).show()
                        Log.w(TAG, "Terrible Error Occurred!: $it")
                    }
            } else {
                Toast.makeText(this@RegisterActivity, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePickerDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
            // Format and display the date in the TextView
            val dateStr = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            binding.tvBirth.text = dateStr

            // Update age when a date is selected
            updateAge(dateStr)  // Call this function here to update the age
        }, year, month, day)

        dpd.show()
    }

    private fun updateAge(birthDate: String) {
        uiScope.launch {
            val age = withContext(Dispatchers.Default) {
                calculateAge(birthDate)  // 백그라운드 스레드에서 나이 계산
            }
            binding.tvAge.text = "만 $age 세"  // UI 스레드에서 TextView 업데이트
        }
    }
    private fun calculateAge(birthDateString: String): Int {
        val parts = birthDateString.split("-")
        if (parts.size < 3) return 0  // 날짜 형식이 잘못되었을 경우 안전하게 처리

        val birthYear = parts[0].toInt()
        val birthMonth = parts[1].toInt()
        val birthDay = parts[2].toInt()

        val today = Calendar.getInstance()
        val currentYear = today.get(Calendar.YEAR)
        val currentMonth = today.get(Calendar.MONTH) + 1  // Calendar.MONTH는 0부터 시작하므로 1을 더해줌
        val currentDay = today.get(Calendar.DAY_OF_MONTH)

        var age = currentYear - birthYear

        // 만약 생일이 아직 지나지 않았다면 나이에서 1을 빼준다
        if (birthMonth > currentMonth || (birthMonth == currentMonth && birthDay > currentDay)) {
            age -= 1
        }

        return age
    }

    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()  // 액티비티가 종료될 때 코루틴 취소
    }

}
