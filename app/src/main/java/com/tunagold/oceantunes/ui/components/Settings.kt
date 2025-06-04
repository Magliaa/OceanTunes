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
    private val settingText2: TextView
    private val settingText3: TextView
    private val settingText4: TextView
    private val settingText5: TextView
    private val settingText6: TextView
    private val settingText7: TextView


    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.Settings)

            LayoutInflater.from(context).inflate(R.layout.item_layout_settings, this, true)


            settingText1 = findViewById(R.id.setting1)
            settingText2 = findViewById(R.id.setting2)
            settingText3 = findViewById(R.id.setting3)
            settingText4 = findViewById(R.id.setting4)
            settingText5 = findViewById(R.id.setting5)
            settingText6 = findViewById(R.id.setting6)
            settingText7 = findViewById(R.id.setting7)


            settingText1.text = attributes.getString(R.styleable.Settings_setting1)
            settingText2.text = attributes.getString(R.styleable.Settings_setting2)
            settingText3.text = attributes.getString(R.styleable.Settings_setting3)
            settingText4.text = attributes.getString(R.styleable.Settings_setting4)
            settingText5.text = attributes.getString(R.styleable.Settings_setting5)
            settingText6.text = attributes.getString(R.styleable.Settings_setting6)
            settingText7.text = attributes.getString(R.styleable.Settings_setting7)

        attributes.recycle()
    }
}