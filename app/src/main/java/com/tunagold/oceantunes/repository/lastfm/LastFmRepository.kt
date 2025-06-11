package com.tunagold.oceantunes.repository.lastfm

import com.tunagold.oceantunes.model.LastFmSearchResponse
import com.tunagold.oceantunes.model.LastFmTrackInfoResponse
import com.tunagold.oceantunes.storage.room.SongDao
import com.tunagold.oceantunes.storage.room.SongRoom
import com.tunagold.oceantunes.utils.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.SerializationException
import javax.inject.Inject

class LastFmRepository @Inject constructor(
    private val httpClient: HttpClient,
    private val songDao: SongDao,
    private val apiKeyProvider: () -> String
) : ILastFmRepository {

    private val baseUrl = "https://ws.audioscrobbler.com/2.0/"
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun searchTracks(query: String): Result<List<SongRoom>> {
        return try {
            val response = httpClient.get(baseUrl) {
                parameter("method", "track.search")
                parameter("track", query)
                parameter("api_key", apiKeyProvider())
                parameter("format", "json")
                parameter("limit", 10)
            }

            if (response.status.value != 200) {
                return Result.Error(Exception("Last.fm API search error: ${response.status.value} - ${response.bodyAsText()}"))
            }

            val lastFmResponse = json.decodeFromString<LastFmSearchResponse>(response.bodyAsText())
            val tracks = lastFmResponse.results.trackmatches.track

            val newSongs = mutableListOf<SongRoom>()
            for (track in tracks) {
                val songId = if (!track.mbid.isNullOrEmpty()) {
                    track.mbid
                } else {
                    val md = java.security.MessageDigest.getInstance("MD5")
                    val hashBytes = md.digest(track.url.toByteArray())
                    hashBytes.joinToString("") { "%02x".format(it) }
                }

                val exists = songDao.getSongById(songId) != null
                if (!exists) {
                    val songRoom = track.toSongRoom(songId)
                    songDao.insertSong(songRoom)
                    newSongs.add(songRoom)
                } else {
                    newSongs.add(songDao.getSongById(songId)!!)
                }
            }
            Result.Success(newSongs)

        } catch (e: SerializationException) {
            Result.Error(Exception("Failed to parse Last.fm search response: ${e.message}"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getTrackInfo(artist: String, track: String): Result<SongRoom?> {
        return try {
            val response = httpClient.get(baseUrl) {
                parameter("method", "track.getInfo")
                parameter("artist", artist)
                parameter("track", track)
                parameter("api_key", apiKeyProvider())
                parameter("format", "json")
            }

            if (response.status.value == 404 || response.status.value == 500) {
                return Result.Success(null)
            }
            if (response.status.value != 200) {
                return Result.Error(Exception("Last.fm API getTrackInfo error: ${response.status.value} - ${response.bodyAsText()}"))
            }

            val lastFmTrackInfoResponse = json.decodeFromString<LastFmTrackInfoResponse>(response.bodyAsText())
            val trackInfo = lastFmTrackInfoResponse.track

            val songId = if (!trackInfo.mbid.isNullOrEmpty()) {
                trackInfo.mbid
            } else {
                val md = java.security.MessageDigest.getInstance("MD5")
                val hashBytes = md.digest(trackInfo.url.toByteArray())
                hashBytes.joinToString("") { "%02x".format(it) }
            }

            val songRoom = trackInfo.toSongRoom(songId)
            songDao.insertSong(songRoom)

            Result.Success(songRoom)

        } catch (e: SerializationException) {
            Result.Error(Exception("Failed to parse Last.fm track info response: ${e.message}"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getTrackInfoByMbid(mbid: String): Result<SongRoom?> {
        return try {
            val response = httpClient.get(baseUrl) {
                parameter("method", "track.getInfo")
                parameter("mbid", mbid)
                parameter("api_key", apiKeyProvider())
                parameter("format", "json")
            }

            if (response.status.value == 404 || response.status.value == 500) {
                return Result.Success(null)
            }
            if (response.status.value != 200) {
                return Result.Error(Exception("Last.fm API getTrackInfoByMbid error: ${response.status.value} - ${response.bodyAsText()}"))
            }

            val lastFmTrackInfoResponse = json.decodeFromString<LastFmTrackInfoResponse>(response.bodyAsText())
            val trackInfo = lastFmTrackInfoResponse.track

            val songId = if (!trackInfo.mbid.isNullOrEmpty()) {
                trackInfo.mbid
            } else {
                val md = java.security.MessageDigest.getInstance("MD5")
                val hashBytes = md.digest(trackInfo.url.toByteArray())
                hashBytes.joinToString("") { "%02x".format(it) }
            }

            val songRoom = trackInfo.toSongRoom(songId)
            songDao.insertSong(songRoom)

            Result.Success(songRoom)

        } catch (e: SerializationException) {
            Result.Error(Exception("Failed to parse Last.fm track info response by MBID: ${e.message}"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}