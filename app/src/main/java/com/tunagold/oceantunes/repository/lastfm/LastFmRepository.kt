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
                parameter("limit", 50)
            }

            if (response.status.value != 200) {
                val errorBody = response.bodyAsText()
                Log.e("LastFmRepo", "Last.fm API search error: ${response.status.value} - $errorBody")
                return Result.Error(Exception("Last.fm API search error: ${response.status.value} - $errorBody"))
            }

            val lastFmResponse = json.decodeFromString<LastFmSearchResponse>(response.bodyAsText())
            val rawTracks = lastFmResponse.results.trackmatches.track

            val newSongs = mutableListOf<SongRoom>()
            for (track in rawTracks) {
                if (track.mbid.isNullOrEmpty()) {
                    Log.d("LastFmRepo", "Skipping track '${track.name}' by '${track.artist}' due to missing MBID.")
                    continue
                }

                val songId = track.mbid

                val existingSong = songDao.getSongById(songId)
                if (existingSong != null && existingSong.image.isNotEmpty() && !existingSong.image.contains("2a96cbd8b46e442fc41c2b86b821562f.png")) {
                    newSongs.add(existingSong)
                    Log.d("LastFmRepo", "Song already in DB with valid image: ${existingSong.title}")
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
            Log.e("LastFmRepo", "Failed to parse Last.fm search response: ${e.message}", e)
            Result.Error(Exception("Failed to parse Last.fm search response: ${e.message}"))
        } catch (e: Exception) {
            Log.e("LastFmRepo", "Error during Last.fm search: ${e.message}", e)
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
                Log.w("LastFmRepo", "Last.fm API returned ${response.status.value} for track info (artist: $artist, track: $track). Treating as not found.")
                return Result.Success(null)
            }
            if (response.status.value != 200) {
                val errorBody = response.bodyAsText()
                Log.e("LastFmRepo", "Last.fm API getTrackInfo error: ${response.status.value} - $errorBody")
                return Result.Error(Exception("Last.fm API getTrackInfo error: ${response.status.value} - $errorBody"))
            }

            val lastFmTrackInfoResponse = json.decodeFromString<LastFmTrackInfoResponse>(response.bodyAsText())
            val trackInfo = lastFmTrackInfoResponse.track // Now this can be null

            if (trackInfo == null || trackInfo.mbid.isNullOrEmpty()) {
                Log.w("LastFmRepo", "No track info found or valid MBID in Last.fm response for (artist: $artist, track: $track).")
                return Result.Success(null)
            }

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
            songDao.insertSong(songRoom) // Insert the fetched song into Room
            Result.Success(songRoom)

        } catch (e: SerializationException) {
            Log.e("LastFmRepo", "Failed to parse Last.fm track info response for (artist: $artist, track: $track): ${e.message}", e)
            Result.Success(null) // Treat as not found by artist/track
        } catch (e: Exception) {
            Log.e("LastFmRepo", "Error during Last.fm getTrackInfo: ${e.message}", e)
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
                Log.w("LastFmRepo", "Last.fm API returned ${response.status.value} for MBID $mbid. Treating as not found.")
                return Result.Success(null)
            }
            if (response.status.value != 200) {
                val errorBody = response.bodyAsText()
                Log.e("LastFmRepo", "Last.fm API getTrackInfoByMbid error: ${response.status.value} - $errorBody")
                return Result.Error(Exception("Last.fm API getTrackInfoByMbid error: ${response.status.value} - $errorBody"))
            }

            val lastFmTrackInfoResponse = json.decodeFromString<LastFmTrackInfoResponse>(response.bodyAsText())
            val trackInfo = lastFmTrackInfoResponse.track

            if (trackInfo == null || trackInfo.mbid.isNullOrEmpty()) {
                Log.w("LastFmRepo", "No track info found or valid MBID in Last.fm response for MBID $mbid. Response may have been empty/error.")
                return Result.Success(null)
            }

            // The rest of your existing logic for processing trackInfo remains the same
            // Use the MBID from the response if available, otherwise generate MD5
            val songIdFromResponse = if (!trackInfo.mbid.isNullOrEmpty()) {
                trackInfo.mbid
            } else {
                val md = java.security.MessageDigest.getInstance("MD5")
                val hashBytes = md.digest(trackInfo.url.toByteArray())
                hashBytes.joinToString("") { "%02x".format(it) }
            }

            var songRoom = trackInfo.toSongRoom(songIdFromResponse)

            if (songRoom.image.isEmpty() || songRoom.image.contains("2a96cbd8b46e442fc41c2b86b821562f.png")) {
                val fallbackImageUrl = imageRepository.searchAlbumArt(songRoom.title, songRoom.artists.firstOrNull() ?: "")
                if (!fallbackImageUrl.isNullOrEmpty()) {
                    songRoom = songRoom.copy(image = fallbackImageUrl)
                }
            }
            songDao.insertSong(songRoom) // Insert the fetched song into Room
            Result.Success(songRoom)

        } catch (e: SerializationException) {
            Log.e("LastFmRepo", "Serialization error for MBID $mbid: ${e.message}", e)
            Result.Success(null)
        } catch (e: Exception) {
            // Catches other general exceptions (network issues, etc.)
            Log.e("LastFmRepo", "General error fetching track info by MBID $mbid: ${e.message}", e)
            Result.Error(e)
        }
    }
}