package com.tunagold.oceantunes.repository.spotify

import com.tunagold.oceantunes.model.SpotifySearchResponse
import com.tunagold.oceantunes.storage.room.SongDao
import com.tunagold.oceantunes.storage.room.SongRoom
import com.tunagold.oceantunes.utils.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText

class SpotifyRepository(
    private val httpClient: HttpClient,
    private val songDao: SongDao,
    private val accessTokenProvider: () -> String
) : ISpotifyRepository {

    override suspend fun searchAndSaveSongs(query: String): Result<List<SongRoom>> {
        return try {
            val response: HttpResponse = httpClient.get("https://api.spotify.com/v1/search") {
                parameter("q", query)
                parameter("type", "track")
                parameter("limit", 10)
                headers {
                    append(HttpHeaders.Authorization, "Bearer ${accessTokenProvider()}")
                }
            }

            val json = Json.decodeFromString<SpotifySearchResponse>(response.bodyAsText())
            val tracks = json.tracks.items

            val newSongs = mutableListOf<SongRoom>()

            for (track in tracks) {
                val exists = songDao.getSongById(track.id) != null
                if (!exists) {
                    val songRoom = track.toSongRoom()
                    songDao.insertSong(songRoom)
                    newSongs.add(songRoom)
                }
            }

            Result.Success(newSongs)

        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
