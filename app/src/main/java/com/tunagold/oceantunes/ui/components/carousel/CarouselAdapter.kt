package com.tunagold.oceantunes.ui.components.carousel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tunagold.oceantunes.R
import com.bumptech.glide.Glide

class CarouselAdapter(
    private val items: List<Triple<String, String, String>>,
    private val onItemClick: (Triple<String, String, String>) -> Unit
) : RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_carousel, parent, false)
        return CarouselViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        val item = items[position]

        val title = item.first
        val imageUrl = item.third

        holder.itemView.findViewById<TextView>(R.id.itemTextView).text = title

        val imageView = holder.itemView.findViewById<ImageView>(R.id.itemImageView)
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.unknown_song_img)
            .error(R.drawable.unknown_song_img)
            .into(imageView)

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class CarouselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}