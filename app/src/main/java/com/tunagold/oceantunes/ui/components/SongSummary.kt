package com.tunagold.oceantunes.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.compose.ui.semantics.text
import com.google.android.material.imageview.ShapeableImageView
import com.tunagold.oceantunes.R

class SongSummary @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val songImage: ShapeableImageView
    private val songTitle: TextView
    private val songArtist: TextView

    private val maxTextLength = 25
    private val ellipsis = "..."

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.SongSummary)

        val isLarge = attributes.getBoolean(R.styleable.SongSummary_isLarge, false)
        val imgResId = attributes.getResourceId(R.styleable.SongSummary_img, R.drawable.unknown_song_img)
        val titleFromAttrs = attributes.getString(R.styleable.SongSummary_title)
        val artistFromAttrs = attributes.getString(R.styleable.SongSummary_artist)

        if (isLarge) {
            LayoutInflater.from(context).inflate(R.layout.item_layout_song_summary_large, this, true)
            songImage = findViewById(R.id.songImageID_large)
            songTitle = findViewById(R.id.songTitleID_large)
            songArtist = findViewById(R.id.songArtistID_large)
        } else {
            LayoutInflater.from(context).inflate(R.layout.item_layout_song_summary, this, true)
            songImage = findViewById(R.id.songImageID)
            songTitle = findViewById(R.id.songTitleID)
            songArtist = findViewById(R.id.songArtistID)
        }

        setImage(imgResId)

        setTitle(titleFromAttrs)
        setArtist(artistFromAttrs)

        attributes.recycle()
    }

    fun setTitle(text: String?) {
        songTitle.text = truncateText(text)
    }

    fun setArtist(text: String?) {
        songArtist.text = truncateText(text)
    }

    fun setImage(resId: Int) {
        songImage.setImageResource(resId)
    }

    private fun truncateText(text: String?): String? {
        if (text == null) {
            return null
        }
        return if (text.length > maxTextLength) {
            text.substring(0, maxTextLength - ellipsis.length) + ellipsis
        } else {
            text
        }
    }
}