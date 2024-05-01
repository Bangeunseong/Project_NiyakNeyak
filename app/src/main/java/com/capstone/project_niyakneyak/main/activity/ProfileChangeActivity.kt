package com.capstone.project_niyakneyak.main.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.capstone.project_niyakneyak.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project_niyakneyak.main.adapter.AvatarAdapter

class ProfileChangeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_change)
        val avatarImages = listOf(R.drawable.boy, R.drawable.girl, R.drawable.man, R.drawable.woman)
        val avatarAdapter = AvatarAdapter(avatarImages)

        val avatarList = findViewById<RecyclerView>(R.id.avatar_list)
        avatarList.layoutManager = GridLayoutManager(this, 3) // Change this line
        avatarList.adapter = avatarAdapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}