package com.tunagold.oceantunes.repository.song

import com.tunagold.oceantunes.storage.room.SongDao
import com.tunagold.oceantunes.storage.room.SongRoom
import com.tunagold.oceantunes.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class SongRepository(private val songDao: SongDao) : ISongRepository {

    override suspend fun getAllSongs(): Result<List<SongRoom>> {
        return try {
            Result.Success(songDao.getAllSongs())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getAllSongsFlow(): Result<Flow<List<SongRoom>>> {
        return Result.Success(songDao.getAllSongsFlow().catch { emit(emptyList()) })
    }

    override suspend fun getSongById(songId: String): Result<SongRoom?> {
        return try {
            Result.Success(songDao.getSongById(songId))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getSongByIdFlow(songId: String): Result<Flow<SongRoom?>> {
        return Result.Success(songDao.getSongByIdFlow(songId).catch { emit(null) })
    }

    override fun getTopRankedSongs(): Result<Flow<List<SongRoom>>> {
        return Result.Success(songDao.getTopRankedSongs().catch { emit(emptyList()) })
    }

    override fun getTopRatedSongs(): Result<Flow<List<SongRoom>>> {
        return Result.Success(songDao.getTopRatedSongs().catch { emit(emptyList()) })
    }

    override fun getMostFavoriteSongs(): Result<Flow<List<SongRoom>>> {
        return Result.Success(songDao.getMostFavoriteSongs().catch { emit(emptyList()) })
    }

    override fun getMostClickedSongs(): Result<Flow<List<SongRoom>>> {
        return Result.Success(songDao.getMostClickedSongs().catch { emit(emptyList()) })
    }

    override suspend fun insertSong(song: SongRoom): Result<Unit> {
        return try {
            songDao.insertSong(song)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun insertAllSongs(vararg songs: SongRoom): Result<Unit> {
        return try {
            songDao.insertAllSongs(*songs)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun delete(song: SongRoom): Result<Unit> {
        return try {
            songDao.delete(song)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteSongById(songId: String): Result<Unit> {
        return try {
            songDao.deleteSongById(songId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteAllSongs(): Result<Unit> {
        return try {
            songDao.deleteAllSongs()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
