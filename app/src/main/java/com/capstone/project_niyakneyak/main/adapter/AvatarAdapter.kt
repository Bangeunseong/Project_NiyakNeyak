package com.capstone.project_niyakneyak.main.adapter

// File: AvatarAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project_niyakneyak.R

class AvatarAdapter(private val avatarImages: List<Int>) : RecyclerView.Adapter<AvatarAdapter.ViewHolder>() {
    var selectedPosition = -1

    inner class ViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView){
        init {
            imageView.setOnClickListener {
                selectedPosition = adapterPosition
                notifyDataSetChanged()
            }
        }
    }
    inner class AvatarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.avatar_image)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val imageView = LayoutInflater.from(parent.context)
            .inflate(R.layout.avatar_item, parent, false) as ImageView
        return ViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setImageResource(avatarImages[position])
        if (position == selectedPosition) {
            holder.imageView.setBackgroundResource(R.drawable.selected_item_border) // Set yellow border
        } else {
            holder.imageView.background = null // Remove border
        }
    }

    override fun getItemCount() = avatarImages.size
}