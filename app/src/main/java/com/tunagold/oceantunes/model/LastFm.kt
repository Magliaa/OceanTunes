package com.tunagold.oceantunes.model

import android.util.Log
import com.tunagold.oceantunes.storage.room.SongRoom
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.security.MessageDigest

@Serializable
data class LastFmSearchResponse(
    val results: LastFmSearchResults
)

@Serializable
data class LastFmSearchResults(
    @SerialName("opensearch:Query")
    val query: LastFmQuery,
    @SerialName("opensearch:totalResults")
    val totalResults: String,
    @SerialName("opensearch:startIndex")
    val startIndex: String,
    @SerialName("opensearch:itemsPerPage")
    val itemsPerPage: String,
    val trackmatches: LastFmTrackMatches
)

@Serializable
data class LastFmQuery(
    val role: String,
    val searchTerms: String? = null,
    val startPage: String
)

@Serializable
data class LastFmTrackMatches(
    val track: List<LastFmTrack>
)

@Serializable
data class LastFmTrack(
    val name: String,
    val artist: String,
    val url: String,
    @SerialName("listeners")
    val listeners: String,
    val image: List<LastFmImage>,
    val mbid: String? = null
) {
    fun toSongRoom(id: String): SongRoom {
        val imageUrl = image.firstOrNull { it.size == "extralarge" }?.text ?:
        image.firstOrNull { it.size == "large" }?.text ?:
        image.firstOrNull { it.size == "medium" }?.text ?:
        ""
        Log.d("LastFmImageDebug", "Track: ${name}, Artist: ${artist}, Image URL: '$imageUrl'")
        return SongRoom(
            id = id,
            title = name,
            artists = listOf(artist),
            album = "",
            image = imageUrl,
            releaseDate = "",
            credits = emptyList()
        )
    }
}

@Serializable
data class LastFmImage(
    @SerialName("#text")
    val text: String,
    val size: String
)

@Serializable
data class LastFmTrackInfoResponse(
    val track: LastFmTrackInfo
)

@Serializable
data class LastFmTrackInfo(
    val name: String,
    val mbid: String?,
    val url: String,
    val listeners: String,
    val playcount: String,
    val artist: LastFmArtistInfo,
    val album: LastFmAlbumInfo?,
    val wiki: LastFmWiki? = null,
    val toptags: LastFmTopTags? = null,
    val image: List<LastFmImage>
) {
    fun toSongRoom(id: String): SongRoom {
        val imageUrl = image.firstOrNull { it.size == "extralarge" }?.text ?:
        image.firstOrNull { it.size == "large" }?.text ?:
        image.firstOrNull { it.size == "medium" }?.text ?:
        ""
        return SongRoom(
            id = id,
            title = name,
            artists = listOf(artist.name),
            album = album?.title ?: "",
            image = imageUrl,
            releaseDate = "",
            credits = emptyList()
        )
    }
}

@Serializable
data class LastFmArtistInfo(
    val name: String,
    val mbid: String? = null,
    val url: String
)

@Serializable
data class LastFmAlbumInfo(
    val artist: String,
    val title: String,
    val mbid: String? = null,
    val url: String,
    val image: List<LastFmImage>
)

@Serializable
data class LastFmWiki(
    val summary: String,
    val content: String
)

@Serializable
data class LastFmTopTags(
    val tag: List<LastFmTag>
)

@Serializable
data class LastFmTag(
    val name: String,
    val url: String
)