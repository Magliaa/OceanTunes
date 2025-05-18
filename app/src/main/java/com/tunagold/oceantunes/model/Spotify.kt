package com.tunagold.oceantunes.model

import com.tunagold.oceantunes.storage.room.SongRoom
import kotlinx.serialization.Serializable

@Serializable
data class SpotifySearchResponse(
    val tracks: Tracks
)

@Serializable
data class Tracks(
    val items: List<SpotifyTrack>
)

@Serializable
data class SpotifyTrack(
    val id: String,
    val name: String,
    val album: Album,
    val artists: List<Artist>,
    val durationMs: Int,
    val externalUrls: ExternalUrls
) {
    fun toSongRoom(): SongRoom {
        val artistNames = artists.map { it.name }
        val imageUrl = album.images.firstOrNull()?.url ?: ""

        return SongRoom(
            id = id,
            title = name,
            artists = artistNames,
            album = album.name,
            image = imageUrl,
            releaseDate = "",
            credits = emptyList(),
            ranking = 0,
            avgScore = 0f,
            favNumber = 0,
            ratersNumber = 0
        )
    }
}

@Serializable
data class Album(
    val id: String,
    val name: String,
    val images: List<Image>
)

@Serializable
data class Artist(
    val id: String,
    val name: String
)

@Serializable
data class Image(
    val url: String,
    val height: Int? = null,
    val width: Int? = null
)

@Serializable
data class ExternalUrls(
    val spotify: String
)
