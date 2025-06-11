package com.tunagold.oceantunes.ui.songsgrid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.tunagold.oceantunes.R
import com.tunagold.oceantunes.model.Song
import com.bumptech.glide.Glide
import android.util.Log // Import Log for debugging

class SongsAdapter(
    private var songs: List<Song>,
    private val onItemClick: ((Song) -> Unit)? = null
) : RecyclerView.Adapter<SongsAdapter.SongViewHolder>() {

    class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songImage: ShapeableImageView = view.findViewById(R.id.songImageID)
        val songTitle: TextView = view.findViewById(R.id.songTitleID)
        val songArtist: TextView = view.findViewById(R.id.songArtistID)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_layout_song_summary, parent, false)
        Log.d("SongsAdapter", "onCreateViewHolder: Inflating item_layout_song_summary")
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        Log.d("SongsAdapter", "onBindViewHolder: Binding song at position $position - Title: ${song.title}, Artist: ${song.artists.joinToString(", ")}, Image: ${song.image}")

        holder.songTitle.text = song.title
        holder.songArtist.text = song.artists.joinToString(", ")

        if (song.image.isNotEmpty()) {
            Glide.with(holder.songImage.context)
                .load(song.image)
                .placeholder(R.drawable.unknown_song_img)
                .error(R.drawable.unknown_song_img)
                .into(holder.songImage)
            Log.d("SongsAdapter", "Glide loading image: ${song.image}")
        } else {
            holder.songImage.setImageResource(R.drawable.unknown_song_img)
            Log.d("SongsAdapter", "Image URL is empty, setting default placeholder.")
        }

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(song)
            Log.d("SongsAdapter", "Item clicked: ${song.title}")
        }
    }

    override fun getItemCount(): Int {
        Log.d("SongsAdapter", "getItemCount: ${songs.size}")
        return songs.size
    }

    fun updateData(newSongs: List<Song>) {
        songs = newSongs
        notifyDataSetChanged()
        Log.d("SongsAdapter", "updateData: New list size ${newSongs.size}. Notifying data set changed.")
    }
}
