package com.tunagold.oceantunes.ui.components

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.tunagold.oceantunes.R
import com.google.android.material.textfield.TextInputLayout

class MaterialTextInput @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.textInputStyle
) : TextInputLayout(context, attrs, defStyleAttr) {

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.MaterialTextInput)
        val hintText = attributes.getString(R.styleable.MaterialTextInput_hintText)
        val textColor = attributes.getColorStateList(R.styleable.MaterialTextInput_textColor)
        val hintTextColor = attributes.getColorStateList(
            R.styleable.MaterialTextInput_hintTextColor)
        val boxStrokeColor = attributes.getColorStateList(
            R.styleable.MaterialTextInput_boxStrokeColor)
        val boxBackgroundColor = attributes.getColorStateList(
            R.styleable.MaterialTextInput_boxBackgroundColor)

        this.hint = hintText
        editText?.setTextColor(textColor ?:
        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.dark)))



        setHintTextColor(hintTextColor)

        if (boxStrokeColor != null) setBoxStrokeColorStateList(boxStrokeColor)
        if (boxBackgroundColor != null) setBoxBackgroundColorStateList(boxBackgroundColor)



        attributes.recycle()
    }
}