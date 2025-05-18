package com.tunagold.oceantunes.repository.spotify

import com.tunagold.oceantunes.storage.room.SongRoom
import com.tunagold.oceantunes.utils.Result

interface ISpotifyRepository {
    suspend fun searchAndSaveSongs(query: String): Result<List<SongRoom>>
}