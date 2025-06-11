package com.tunagold.oceantunes.storage.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy // Import OnConflictStrategy
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM Song")
    suspend fun getAllSongs(): List<SongRoom>

    @Query("SELECT * FROM Song")
    fun getAllSongsFlow(): Flow<List<SongRoom>>

    @Query("SELECT * FROM Song WHERE id = :songId")
    suspend fun getSongById(songId: String): SongRoom?

    @Query("SELECT * FROM Song WHERE id = :songId")
    fun getSongByIdFlow(songId: String): Flow<SongRoom?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Added onConflictStrategy
    suspend fun insertSong(song: SongRoom)

    @Insert(onConflict = OnConflictStrategy.REPLACE) // Added onConflictStrategy
    suspend fun insertAllSongs(vararg songs: SongRoom)

    @Delete
    suspend fun delete(song: SongRoom)

    @Query("DELETE FROM Song WHERE id = :songId")
    suspend fun deleteSongById(songId: String)

    @Query("DELETE FROM Song")
    suspend fun deleteAllSongs()

    @Query("SELECT * FROM Song WHERE id IN (:songIds)")
    fun getSongsByIds(songIds: List<String>): Flow<List<SongRoom>>
}
