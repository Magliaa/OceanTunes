package com.tunagold.oceantunes.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.tunagold.oceantunes.R

@SuppressLint("SetTextI18n")
class Settings @JvmOverloads constructor (
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {


    private val settingText1: TextView
    private val settingText4: TextView


    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.Settings)
        LayoutInflater.from(context).inflate(R.layout.item_layout_settings, this, true)

        settingText1 = findViewById(R.id.setting1)
        settingText4 = findViewById(R.id.setting4)

        listOf(
            settingText1 to attributes.getString(R.styleable.Settings_setting1),
            settingText4 to attributes.getString(R.styleable.Settings_setting4),
        ).forEach { (textView, value) ->
            if (value.isNullOrBlank()) {
                textView.visibility = GONE
            } else {
                textView.text = value
            }
        }

        attributes.recycle()
    }

}