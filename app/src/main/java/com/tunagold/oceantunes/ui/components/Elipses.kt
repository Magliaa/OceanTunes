package com.tunagold.oceantunes.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.tunagold.oceantunes.R

class Elipses @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatImageButton (context, attrs, defStyleAttr) {



    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.Elipses)

        val elipsesIcon = attributes.getResourceId(R.styleable.Elipses_elipsesIcon, R.drawable.fab_logo_temp)

        setImageResource(elipsesIcon)

        val settings = findViewById<Settings>(R.id.settingsCard)

        settings.visibility = View.GONE

        setOnClickListener {
            settings.visibility =
                if(settings.visibility == View.VISIBLE
            ) View.GONE else View.VISIBLE
        }



        attributes.recycle()
    }




}