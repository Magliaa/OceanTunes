package com.tunagold.oceantunes.ui.components

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.tunagold.oceantunes.R
import com.google.android.material.button.MaterialButton

class MaterialButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.materialButtonStyle
) : MaterialButton(context, attrs, defStyleAttr) {

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.MaterialButton)
        val buttonText = attributes.getString(R.styleable.MaterialButton_buttonText)
        val buttonColor = attributes.getColorStateList(R.styleable.MaterialButton_buttonColor)
        val setIcon = attributes.getBoolean(R.styleable.MaterialButton_setIcon, false)

        text = buttonText ?: "DEFAULT TEXT BUTTON"
        textSize = resources.getDimension(R.dimen.button_text_size)
        cornerRadius = resources.getDimensionPixelSize(R.dimen.button_corner_radius)
        backgroundTintList = buttonColor ?:
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.purple3))

        if (backgroundTintList == buttonColor &&
            buttonColor != ColorStateList.valueOf(ContextCompat.getColor(context, R.color.purple3))) {
            setTextColor(ContextCompat.getColor(context, R.color.dark))
        } else {
            setTextColor(ContextCompat.getColor(context, R.color.white))
        }

        if (setIcon) {
            icon = ContextCompat.getDrawable(context, R.drawable.ic_arrow_right)
            iconSize = resources.getDimensionPixelSize(R.dimen.button_icon_size)

            iconTint = if (backgroundTintList == buttonColor &&
                buttonColor != ColorStateList.valueOf(ContextCompat.getColor(context, R.color.purple3))) {
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.purple2))
            } else {
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
            }
        }

        attributes.recycle()
    }
}