package com.capstone.project_niyakneyak.main.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.project_niyakneyak.databinding.ActivityResultBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class ResultActivity: AppCompatActivity() {
    // Params of view binding
    private var _binding: ActivityResultBinding? = null
    private val binding get() = _binding!!

    // Params about Firebase
    private var _firestore: FirebaseFirestore? = null
    private var _firebaseAuth: FirebaseAuth? = null
    private val firestore get() = _firestore!!
    private val firebaseAuth get() = _firebaseAuth!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _firestore = Firebase.firestore
        _firebaseAuth = Firebase.auth
        _binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onDestroy() {
        super.onDestroy()

        _firestore = null
        _firebaseAuth = null
    }
}