package com.tunagold.oceantunes.ui.components

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tunagold.oceantunes.R

class MaterialFAB @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.floatingActionButtonStyle
) : FloatingActionButton(context, attrs, defStyleAttr) {

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.MaterialFAB)
        val borderRadius = resources.getDimensionPixelSize(R.dimen.FAB_corner_radius)
        val backgroundColor = attributes.getColor(R.styleable.MaterialFAB_backgroundColor, Color.WHITE)
        val icon = attributes.getResourceId(R.styleable.MaterialFAB_icon, 0)
        val iconTint = attributes.getColor(R.styleable.MaterialFAB_iconTint, Color.BLACK)

        shapeAppearanceModel = shapeAppearanceModel.toBuilder()
            .setAllCornerSizes(borderRadius.toFloat())
            .build()

        backgroundTintList = ColorStateList.valueOf(backgroundColor)
        imageTintList = ColorStateList.valueOf(iconTint)

        if (icon != 0) {
            setImageResource(icon)
        }

        attributes.recycle()
    }
}