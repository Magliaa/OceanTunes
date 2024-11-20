package com.tunagold.oceantunes.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.material.imageview.ShapeableImageView
import com.tunagold.oceantunes.R

class TopChartsSong @JvmOverloads constructor (
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val songImage: ShapeableImageView
    private val songTitle: TextView
    private val songArtist: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.item_layout_top_charts_song, this, true)

        songImage = findViewById(R.id.topChartsSongImageID)
        songTitle = findViewById(R.id.topChartsSongTitleID)
        songArtist = findViewById(R.id.topChartsSongArtistID)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.TopChartsSong)

        val img = attributes.getResourceId(R.styleable.TopChartsSong_imgchartssong, R.drawable.unknown_song_img)
        val title = attributes.getString(R.styleable.TopChartsSong_titlechartssong)
        val artist = attributes.getString(R.styleable.TopChartsSong_artistchartssong)

        songImage.setImageResource(img)
        songTitle.text = title
        songArtist.text = artist

        attributes.recycle()
    }

}