package com.tunagold.oceantunes.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.material.imageview.ShapeableImageView
import com.tunagold.oceantunes.R

class SongSummary @JvmOverloads constructor (
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val songImage: ShapeableImageView
    private val songTitle: TextView
    private val songArtist: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.item_layout_song_summary, this, true)

        songImage = findViewById(R.id.songImageID)
        songTitle = findViewById(R.id.songTitleID)
        songArtist = findViewById(R.id.songArtistID)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.SongSummary)

        val img = attributes.getResourceId(R.styleable.SongSummary_img, R.drawable.unknown_song_img)
        val title = attributes.getString(R.styleable.SongSummary_title)
        val artist = attributes.getString(R.styleable.SongSummary_artist)

        songImage.setImageResource(img)
        songTitle.text = title
        songArtist.text = artist

        attributes.recycle()
    }

}