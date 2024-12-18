package com.tunagold.oceantunes.ui.components

import android.content.Context
import android.util.AttributeSet
import com.tunagold.oceantunes.R

class Elipses @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.materialButtonStyle
) : androidx.appcompat.widget.AppCompatImageButton (context, attrs, defStyleAttr) {

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.Elipses)

        val elipsesIcon = attributes.getResourceId(R.styleable.Elipses_elipsesIcon, R.drawable.fab_logo_temp)

        setImageResource(elipsesIcon)
        setOnClickListener {
            // Codice per aprire il drawer
        }
        attributes.recycle()
    }

}