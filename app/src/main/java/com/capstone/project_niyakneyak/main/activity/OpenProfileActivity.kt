package com.capstone.project_niyakneyak.main.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.databinding.ActivityOpenProfileBinding
import com.google.firebase.firestore.toObject
import com.capstone.project_niyakneyak.login.activity.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class OpenProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOpenProfileBinding
    private lateinit var firestore: FirebaseFirestore
    private var user: FirebaseUser? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpenProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth // Firebase Auth 초기화
        firestore = Firebase.firestore // Firestore 초기화
        user = auth?.currentUser // 현재 사용자 가져오기

        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId).get().addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject<UserAccount>()
                val age = user?.age.toString()
                if (user != null) {
                    binding.yourCurrentNameTextview.text = user.name
                    binding.TextName.text = user.name
                    binding.editTextAge.text = "만 $age 세"
                    binding.GenderTextView.text = user.gender
                    binding.IDTextView.text = user.emailId
                    binding.PWTextView.text = user.password
                    binding.PNTextView.text = user.phoneNum
                    binding.BDTextView.text = user.birth
                }
            }.addOnFailureListener { exception ->
                Log.e("Firestore", "Error getting user data: ", exception)
            }
        }

        binding.modifyButton.setOnClickListener {
            startActivity(Intent(this, SetProfileActivity::class.java))
        }

        binding.logoutButton.setOnClickListener {
            auth?.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}