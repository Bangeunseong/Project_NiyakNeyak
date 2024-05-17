package com.capstone.project_niyakneyak.login.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.project_niyakneyak.R
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var editTextEmail: EditText
    private lateinit var buttonSendResetEmail: Button
    private lateinit var buttonBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Bind views
        editTextEmail = findViewById(R.id.resetPw_IdEditText)
        buttonSendResetEmail = findViewById(R.id.resetPwBtn)
        buttonBack = findViewById(R.id.cancelBtn)

        // Set up button listeners
        buttonSendResetEmail.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                sendPasswordResetEmail(email)
            } else {
                Toast.makeText(this, "Please enter an email address.", Toast.LENGTH_SHORT).show()
            }
        }

        buttonBack.setOnClickListener {
            // Finish the activity to return to the previous one
            finish()
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Failed to send reset email: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
