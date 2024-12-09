package com.tunagold.oceantunes.ui.components

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.google.android.material.divider.MaterialDivider
import com.tunagold.oceantunes.R

class MaterialDivider @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialDivider (context, attrs, defStyleAttr) {

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.Divider)

        val thickness = resources.getDimensionPixelSize(R.dimen.divider_thickness)

        dividerInsetStart = thickness
        dividerInsetEnd = thickness
        dividerColor = attributes.getColor(R.styleable.Divider_color, Color.GRAY)

        attributes.recycle()
    }
}