package com.capstone.project_niyakneyak.main.adapter

// File: AvatarAdapter.kt
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project_niyakneyak.R

class AvatarAdapter(private val avatarImages: List<Int>) : RecyclerView.Adapter<AvatarAdapter.ViewHolder>() {

    class ViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val imageView = LayoutInflater.from(parent.context)
            .inflate(R.layout.avatar_item, parent, false) as ImageView
        return ViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setImageResource(avatarImages[position])
    }

    override fun getItemCount() = avatarImages.size
}