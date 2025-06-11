package com.tunagold.oceantunes.repository.lastfm

import com.tunagold.oceantunes.model.LastFmSearchResponse
import com.tunagold.oceantunes.model.LastFmTrackInfoResponse
import com.tunagold.oceantunes.repository.images.IImageRepository
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
import android.util.Log

class LastFmRepository @Inject constructor(
    private val httpClient: HttpClient,
    private val songDao: SongDao,
    private val apiKeyProvider: () -> String,
    private val imageRepository: IImageRepository
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

                val existingSong = songDao.getSongById(songId)
                if (existingSong != null && existingSong.image.isNotEmpty() && !existingSong.image.contains("2a96cbd8b46e442fc41c2b86b821562f.png")) {
                    newSongs.add(existingSong)
                    Log.d("LastFmRepo", "Song already in DB with image: ${existingSong.title}")
                    continue
                }

                var songRoom = track.toSongRoom(songId)

                if (songRoom.image.isEmpty() || songRoom.image.contains("2a96cbd8b46e442fc41c2b86b821562f.png")) {
                    Log.d("LastFmRepo", "Last.fm image missing or generic for ${songRoom.title}. Trying fallback.")
                    val fallbackImageUrl = imageRepository.searchAlbumArt(songRoom.title, songRoom.artists.firstOrNull() ?: "")
                    if (!fallbackImageUrl.isNullOrEmpty()) {
                        Log.d("LastFmRepo", "Fallback image found for ${songRoom.title}: $fallbackImageUrl")
                        songRoom = songRoom.copy(image = fallbackImageUrl)
                    } else {
                        Log.d("LastFmRepo", "No fallback image found for ${songRoom.title}.")
                    }
                } else {
                    Log.d("LastFmRepo", "Last.fm provided valid image for ${songRoom.title}: ${songRoom.image}")
                }

                songDao.insertSong(songRoom)
                newSongs.add(songRoom)
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

            var songRoom = trackInfo.toSongRoom(songId)

            if (songRoom.image.isEmpty() || songRoom.image.contains("2a96cbd8b46e442fc41c2b86b821562f.png")) {
                val fallbackImageUrl = imageRepository.searchAlbumArt(songRoom.title, songRoom.artists.firstOrNull() ?: "")
                if (!fallbackImageUrl.isNullOrEmpty()) {
                    songRoom = songRoom.copy(image = fallbackImageUrl)
                }
            }
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

            var songRoom = trackInfo.toSongRoom(songId)

            if (songRoom.image.isEmpty() || songRoom.image.contains("2a96cbd8b46e442fc41c2b86b821562f.png")) {
                val fallbackImageUrl = imageRepository.searchAlbumArt(songRoom.title, songRoom.artists.firstOrNull() ?: "")
                if (!fallbackImageUrl.isNullOrEmpty()) {
                    songRoom = songRoom.copy(image = fallbackImageUrl)
                }
            }
            songDao.insertSong(songRoom)
            Result.Success(songRoom)

        } catch (e: SerializationException) {
            Result.Error(Exception("Failed to parse Last.fm track info response by MBID: ${e.message}"))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}