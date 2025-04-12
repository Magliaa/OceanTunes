package com.tunagold.oceantunes.ui.songsgrid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.tunagold.oceantunes.R

class SongsAdapter(private var songs: List<Triple<String, String, Int>>) :
    RecyclerView.Adapter<SongsAdapter.SongViewHolder>() {

    // ViewHolder personalizzato
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
        holder.songTitle.text = song.first // Titolo
        holder.songArtist.text = song.second // Artista
        holder.songImage.setImageResource(song.third) // Immagine
    }

    override fun getItemCount(): Int = songs.size

    // 🔁 Metodo per aggiornare i dati
    fun updateData(newSongs: List<Triple<String, String, Int>>) {
        songs = newSongs
        notifyDataSetChanged()
    }
}
