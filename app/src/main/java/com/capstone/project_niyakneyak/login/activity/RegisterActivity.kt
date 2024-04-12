package com.capstone.project_niyakneyak.login.activity

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    companion object {
        private const val TAG = "REGISTER_ACTIVITY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth and Firestore
        mFirebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

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

                mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd)
                    .addOnSuccessListener(this@RegisterActivity) {
                        val firebaseUser = mFirebaseAuth.currentUser
                        val account = UserAccount(
                            firebaseUser?.uid,
                            firebaseUser?.email,
                            strPwd,
                            binding.etName.text.toString(),
                            birthDate,
                            selectedGender,
                            binding.etPhoneNum.text.toString()
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
        }, year, month, day)

        dpd.show()
    }
}
