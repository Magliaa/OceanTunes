package com.tunagold.oceantunes.repository.song

import com.tunagold.oceantunes.storage.room.SongRoom
import com.tunagold.oceantunes.utils.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieval of [Song] from a given data source.
 */
interface ISongRepository {
    /**
     * Retrieve all the songs from the given data source.
     */
    suspend fun getAllSongs(): Result<List<SongRoom>>

    /**
     * Retrieve the flow of all songs from the given data source.
     */
    fun getAllSongsFlow(): Result<Flow<List<SongRoom>>>

    /**
     * Retrieve a song by its ID from the given data source.
     */
    suspend fun getSongById(songId: String): Result<SongRoom?>

    /**
     * Retrieve the flow of a song by its ID from the given data source.
     */
    fun getSongByIdFlow(songId: String): Result<Flow<SongRoom?>>

    /**
     * Insert a song into the data source.
     */
    suspend fun insertSong(song: SongRoom): Result<Unit>

    /**
     * Insert multiple songs into the data source.
     */
    suspend fun insertAllSongs(vararg songs: SongRoom): Result<Unit>

    /**
     * Delete a song from the data source.
     */
    suspend fun delete(song: SongRoom): Result<Unit>

    /**
     * Delete a song by its ID from the data source.
     */
    suspend fun deleteSongById(songId: String): Result<Unit>

    /**
     * Delete all songs from the data source.
     */
    suspend fun deleteAllSongs(): Result<Unit>

    /**
     * Retrieve the flow of songs by a list of their IDs from the given data source.
     */
    fun getSongsByIds(songIds: List<String>): Flow<List<SongRoom>>
}
