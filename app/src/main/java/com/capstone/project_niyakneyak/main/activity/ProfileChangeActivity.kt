package com.capstone.project_niyakneyak.main.activity

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.capstone.project_niyakneyak.R
import androidx.recyclerview.widget.RecyclerView

class ProfileChangeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_change)


        val setprofileButton = findViewById<Button>(R.id.set_profile_button)
        setprofileButton.setOnClickListener {
            // Set selectedImage as profile image
        }
        val galleryButton = findViewById<Button>(R.id.select_from_gallery_button)
        galleryButton.setOnClickListener {
            // Set selectedImage as profile image
        }
        val backButton = findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
    }
}