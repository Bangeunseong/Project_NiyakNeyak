package com.capstone.project_niyakneyak.main.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.main.adapter.ImageSliderAdapter

class HowToUseActivity : AppCompatActivity() {

    private val images = intArrayOf(
        R.drawable.how_to_img1,
        R.drawable.how_to_img2,
        R.drawable.how_to_img3,
        R.drawable.how_to_img4
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to_use)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val dotIndicatorLayout: LinearLayout = findViewById(R.id.dotIndicatorLayout)
        val closeButton: ImageView = findViewById(R.id.closeButton)

        val adapter = ImageSliderAdapter(this, images)
        viewPager.adapter = adapter

        setupDotIndicators(dotIndicatorLayout, images.size)
        updateDotIndicators(dotIndicatorLayout, 0)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDotIndicators(dotIndicatorLayout, position)
            }
        })

        closeButton.setOnClickListener {
            finish() // 액티비티 종료
        }
    }

    private fun setupDotIndicators(dotIndicatorLayout: LinearLayout, count: Int) {
        val dots = Array(count) { ImageView(this) }
        val size = resources.getDimensionPixelSize(R.dimen.dot_size)
        val margin = resources.getDimensionPixelSize(R.dimen.dot_margin)
        for (i in dots.indices) {
            dots[i] = ImageView(this).apply {
                setImageResource(R.drawable.dot_inactive)
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    setMargins(margin, 0, margin, 0)
                }
            }
            dotIndicatorLayout.addView(dots[i])
        }
    }

    private fun updateDotIndicators(dotIndicatorLayout: LinearLayout, position: Int) {
        for (i in 0 until dotIndicatorLayout.childCount) {
            val imageView = dotIndicatorLayout.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageResource(R.drawable.dot_active)
            } else {
                imageView.setImageResource(R.drawable.dot_inactive)
            }
        }
    }
}