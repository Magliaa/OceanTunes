package com.tunagold.oceantunes.ui.components

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRatingBar
import android.widget.Toast


class MaterialRating @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.ratingBarStyle
) : AppCompatRatingBar(context, attrs, defStyleAttr) {

    init {
        // Set number of stars
        numStars = 5
        stepSize = 0.5f
        setIsIndicator(false)


        // Set listener for rating changes
        onRatingBarChangeListener = OnRatingBarChangeListener { _, rating, _ ->
            Toast.makeText(context, "Rating: $rating", Toast.LENGTH_SHORT).show()
            // Handle rating change here, e.g., store the rating value
        }
    }

    // Add any custom methods or properties here
    fun getRatingValue(): Float {
        return rating
    }

    fun setRatingValue(rating: Float) {
        this.rating = rating
    }
}