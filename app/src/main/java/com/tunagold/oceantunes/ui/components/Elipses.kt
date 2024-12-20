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

    private var settings: View? = null

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.Elipses)
        val elipsesIcon = attributes.getResourceId(R.styleable.Elipses_elipsesIcon, R.drawable.ic_vertdots_fillblack_24dp)

        setImageResource(elipsesIcon)

        attributes.recycle()

        // Set click event to toggle the visibility of the settings card
        setOnClickListener {
            settings?.let {
                it.visibility =
                    if (it.visibility == View.VISIBLE) View.GONE
                    else View.VISIBLE
            }
        }
    }

    // Attach the listener when the view is attached to the window
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val parentView = parent as? View
        settings = parentView?.findViewById(R.id.settingsCard)
        settings?.visibility = View.GONE
    }
}