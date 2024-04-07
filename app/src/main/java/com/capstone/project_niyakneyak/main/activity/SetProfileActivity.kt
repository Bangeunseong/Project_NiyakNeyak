package com.capstone.project_niyakneyak.main.activity

import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.databinding.ActivitySetProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class SetProfileActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySetProfileBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySetProfileBinding.inflate(layoutInflater)

        setContentView(binding.root)

        firestore = Firebase.firestore
        val bundle = intent
        userId = bundle.getStringExtra(UserAccount.REPRESENT_KEY).toString()
        if(userId != "null"){
            firestore.collection(UserAccount.COLLECTION_ID).document(userId).get()
                .addOnSuccessListener {
                    val userAccount = it.toObject<UserAccount>()
                    binding.editTextName.setText(userAccount!!.name)
                }.addOnFailureListener {
                    setResult(RESULT_CANCELED)
                    finish()
                }
        }
        binding.itemModifyButton.setOnClickListener {
            val newName = binding.editTextName.text.toString()
            val selectedGenderId = binding.genderRadioGroup.checkedRadioButtonId
            val selectedGender = if (selectedGenderId != -1) findViewById<RadioButton>(selectedGenderId).text.toString() else null

            if (newName.isNotEmpty() && selectedGender != null) {
                val userUpdates = mapOf(
                    "name" to newName,
                    "gender" to selectedGender
                )

                // 현재 로그인한 사용자의 ID를 사용하여 Firestore 문서 업데이트
                if(userId != "null"){
                    firestore.collection(UserAccount.COLLECTION_ID).document(userId).update(userUpdates)
                        .addOnSuccessListener {
                            setResult(RESULT_OK)
                            Toast.makeText(this, "성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            setResult(RESULT_CANCELED)
                            finish()
                        }
                }
            } else {
                Toast.makeText(this, "이름과 나이를 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show()
            }

        }

        binding.cancelButton.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }


}