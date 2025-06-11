package com.tunagold.oceantunes.model

import kotlinx.serialization.Serializable

@Serializable
data class iTunesSearchResponse(
    val resultCount: Int,
    val results: List<iTunesResult>
)

@Serializable
data class iTunesResult(
    val artistName: String,
    val trackName: String,
    val artworkUrl30: String,
    val artworkUrl60: String,
    val artworkUrl100: String
)