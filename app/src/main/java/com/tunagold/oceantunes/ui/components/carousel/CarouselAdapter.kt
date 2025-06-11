package com.tunagold.oceantunes.ui.components.carousel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tunagold.oceantunes.R
import com.bumptech.glide.Glide
import com.tunagold.oceantunes.model.Song // Import your Song model here

class CarouselAdapter<T>( // Added generic type <T>
    private var items: List<Triple<String, String, T>>, // Make 'items' a 'var' so it can be updated
    private val onItemClick: (Triple<String, String, T>) -> Unit
) : RecyclerView.Adapter<CarouselAdapter<T>.CarouselViewHolder>() {

    // New method to update the adapter's data
    fun updateData(newItems: List<Triple<String, String, T>>) {
        this.items = newItems
        notifyDataSetChanged() // Notify the RecyclerView that the data has changed
        // For better performance with large lists, consider using DiffUtil
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_carousel, parent, false)
        return CarouselViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        val item = items[position]

        val title = item.first
        val songObject = item.third // This is T, which we expect to be a Song

        val imageUrl: String? = if (songObject is Song) {
            songObject.image
        } else {
            songObject as? String
        }


        holder.itemView.findViewById<TextView>(R.id.itemTextView).text = title

        val imageView = holder.itemView.findViewById<ImageView>(R.id.itemImageView)
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.unknown_song_img)
                .error(R.drawable.unknown_song_img)
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.unknown_song_img)
        }


        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class CarouselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // No specific views to initialize here if you find them in onBindViewHolder.
    }
}