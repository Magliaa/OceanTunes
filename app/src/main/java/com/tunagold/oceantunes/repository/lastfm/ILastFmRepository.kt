package com.tunagold.oceantunes.repository.lastfm

import com.tunagold.oceantunes.storage.room.SongRoom
import com.tunagold.oceantunes.utils.Result

interface ILastFmRepository {
    suspend fun searchTracks(query: String): Result<List<SongRoom>>
    suspend fun getTrackInfo(artist: String, track: String): Result<SongRoom?>
    suspend fun getTrackInfoByMbid(mbid: String): Result<SongRoom?>
}
