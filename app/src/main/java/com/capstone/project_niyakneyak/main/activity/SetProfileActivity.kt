package com.capstone.project_niyakneyak.main.activity

import android.os.Bundle
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

        firestore = Firebase.firestore
        val bundle = intent
        userId = bundle.getStringExtra(UserAccount.REPRESENT_KEY).toString()
        if(userId != "null"){
            firestore.collection(UserAccount.COLLECTION_ID).document(userId).get()
                .addOnSuccessListener {
                    val userAccount = it.toObject<UserAccount>()
                    binding.editTextText.setText(userAccount!!.name)
                }.addOnFailureListener {
                    setResult(RESULT_CANCELED)
                    finish()
                }
        }
    }
}