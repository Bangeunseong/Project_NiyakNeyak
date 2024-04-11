package com.capstone.project_niyakneyak.main.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.main.fragment.SettingFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class OpenProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_profile)
        val idToken = intent.getStringArrayExtra(UserAccount.REPRESENT_KEY)


        val modifyButton = findViewById<Button>(R.id.modify_button)
        modifyButton.setOnClickListener {
            val intent = Intent(this, SetProfileActivity::class.java).apply{
                putExtra(UserAccount.REPRESENT_KEY, idToken)
            }
            startActivity(intent)
        }
    }

    //

}