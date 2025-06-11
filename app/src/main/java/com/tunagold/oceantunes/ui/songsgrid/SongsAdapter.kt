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
import androidx.recyclerview.widget.DiffUtil
import android.util.Log

class SongsAdapter(
    private var songs: List<Song>,
    private val onItemClick: ((Song) -> Unit)? = null
) : RecyclerView.Adapter<SongsAdapter.SongViewHolder>() {

    val currentList: List<Song>
        get() = songs

    class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songImage: ShapeableImageView = view.findViewById(R.id.songImageID)
        val songTitle: TextView = view.findViewById(R.id.songTitleID)
        val songArtist: TextView = view.findViewById(R.id.songArtistID)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_layout_song_summary, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]

        holder.songTitle.text = song.title
        holder.songArtist.text = song.artists.joinToString(", ")

        if (song.image.isNotEmpty() && !song.image.contains("2a96cbd8b46e442fc41c2b86b821562f.png")) {
            Glide.with(holder.songImage.context)
                .load(song.image)
                .placeholder(R.drawable.unknown_song_img)
                .error(R.drawable.unknown_song_img)
                .into(holder.songImage)
            Log.d("SongsAdapter", "Glide loading image: ${song.image}")
        } else {
            holder.songImage.setImageResource(R.drawable.unknown_song_img)
            Log.d("SongsAdapter", "Image URL is empty or generic, setting default placeholder.")
        }

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(song)
        }
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    fun updateData(newSongs: List<Song>) {
        val diffCallback = SongDiffCallback(this.songs, newSongs)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.songs = newSongs
        diffResult.dispatchUpdatesTo(this)
    }

    private class SongDiffCallback(
        private val oldList: List<Song>,
        private val newList: List<Song>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldSong = oldList[oldItemPosition]
            val newSong = newList[newItemPosition]

            return oldSong.id == newSong.id &&
                    oldSong.title == newSong.title &&
                    oldSong.album == newSong.album &&
                    oldSong.image == newSong.image &&
                    oldSong.releaseDate == newSong.releaseDate &&
                    oldSong.artists == newSong.artists &&
                    oldSong.credits == newSong.credits
        }
    }
}