package com.tunagold.oceantunes.model

import kotlinx.serialization.Serializable

@Serializable
data class GlobalSongStats(
    val id: String = "",
    val totalPlayCount: Long = 0,
    val totalFavoriteCount: Long = 0,
    val sumOfRatings: Double = 0.0, // Sum of all ratings
    val totalRatedCount: Long = 0, //N Number of users who rated
    val ranking: Int = 0 // This might be calculated client-side or by a function
) {
    val avgScore: Float
        get() = if (totalRatedCount > 0) (sumOfRatings / totalRatedCount).toFloat() else 0.0f
}
