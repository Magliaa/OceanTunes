package com.tunagold.oceantunes.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.tunagold.oceantunes.R

@SuppressLint("SetTextI18n")
class DataBox @JvmOverloads constructor (
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val valText1: TextView
    private val valText2: TextView


    private val titleText1: TextView
    private val titleText2: TextView


    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.DataBox)

        val is3Segments = attributes.getBoolean(R.styleable.DataBox_is3Segments, false)

        val val1 = attributes.getString(R.styleable.DataBox_val1)
        val val2 = attributes.getString(R.styleable.DataBox_val2)
        val val3 = attributes.getString(R.styleable.DataBox_val3)

        val title1 = attributes.getString(R.styleable.DataBox_title1)
        val title2 = attributes.getString(R.styleable.DataBox_title2)
        val title3 = attributes.getString(R.styleable.DataBox_title3)

        if(is3Segments) {

            LayoutInflater.from(context).inflate(R.layout.item_layout_data_box_large, this, true)

            valText1 = findViewById(R.id.val1)
            valText2 = findViewById(R.id.val2)

            val valText3: TextView = findViewById(R.id.val3)

            titleText1 = findViewById(R.id.title1)
            titleText2 = findViewById(R.id.title2)

            val titleText3: TextView = findViewById(R.id.title3)

            valText1.text = val1
            valText3.text = val3

            if (title2 == "Ranking") {
                valText2.text = "#$val2"
            } else {
                valText2.text = val2
            }

            titleText1.text = title1
            titleText2.text = title2
            titleText3.text = title3

        } else {
            //LayoutInflater.from(context).inflate(R.layout.item_layout_data_box, this, true)

            valText1 = findViewById(R.id.val1)
            valText2 = findViewById(R.id.val2)
            titleText1 = findViewById(R.id.title1)
            titleText2 = findViewById(R.id.title2)

            valText1.text = val1
            valText2.text = val2
            titleText1.text = title1
            titleText2.text = title2
        }

        attributes.recycle()
    }
}