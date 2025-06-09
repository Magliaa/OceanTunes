package com.tunagold.oceantunes.model

data class UserSongInteraction(
    val songId: String = "",
    val userId: String = "",
    val rating: Float? = null,
    val isFavorite: Boolean = false,
)
