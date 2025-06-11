package com.tunagold.oceantunes.repository.images

import com.tunagold.oceantunes.model.iTunesSearchResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import javax.inject.Inject
import android.util.Log

interface IImageRepository {
    suspend fun searchAlbumArt(title: String, artist: String): String?
}

class ITunesImageRepository @Inject constructor(
    private val httpClient: HttpClient
) : IImageRepository {

    private val baseUrl = "https://itunes.apple.com/search"
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun searchAlbumArt(title: String, artist: String): String? {
        return try {
            val query = "$title $artist"
            val response = httpClient.get(baseUrl) {
                parameter("term", query)
                parameter("entity", "song")
                parameter("limit", 1)
            }

            if (response.status.value != 200) {
                Log.e("iTunesImageRepo", "iTunes API error: ${response.status.value} - ${response.bodyAsText()}")
                return null
            }

            val iTunesResponse = json.decodeFromString<iTunesSearchResponse>(response.bodyAsText())
            val artworkUrl = iTunesResponse.results.firstOrNull()?.artworkUrl100

            val largeArtworkUrl = artworkUrl?.replace("100x100bb.jpg", "600x600bb.jpg")
                ?: artworkUrl

            Log.d("iTunesImageRepo", "Searched for '$query', found image: '$largeArtworkUrl'")
            largeArtworkUrl

        } catch (e: Exception) {
            Log.e("iTunesImageRepo", "Error searching iTunes album art: ${e.message}", e)
            null
        }
    }
}