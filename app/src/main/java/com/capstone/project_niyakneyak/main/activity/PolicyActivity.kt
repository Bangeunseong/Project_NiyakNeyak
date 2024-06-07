package com.capstone.project_niyakneyak.main.activity

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.capstone.project_niyakneyak.R

class PolicyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_policy)

        val btnBack: Button = findViewById(R.id.btn_back)

        btnBack.setOnClickListener {
            // 뒤로가기 버튼 클릭 시 현재 Activity를 종료
            finish()
        }
    }
}
