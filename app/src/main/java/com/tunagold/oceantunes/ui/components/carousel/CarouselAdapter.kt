package com.tunagold.oceantunes.ui.components.carousel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tunagold.oceantunes.R

class CarouselAdapter(
    private val items: List<Any>,
    private val onItemClick: (Any) -> Unit
) : RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_carousel, parent, false) // Replace with your item layout
        return CarouselViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        val item = items[position]

        if (item is Triple<*, *, *>) {
            val title = item.first as? String ?: ""
            val imageRes = item.third as? Int ?: R.drawable.unknown_song_img

            holder.itemView.findViewById<TextView>(R.id.itemTextView).text = title
            holder.itemView.findViewById<ImageView>(R.id.itemImageView).setImageResource(imageRes)

            holder.itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class CarouselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Get references to views in the item layout
        // ...
    }
}