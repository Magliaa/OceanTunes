package com.tunagold.oceantunes.repository.song

import com.tunagold.oceantunes.repository.lastfm.ILastFmRepository
import com.tunagold.oceantunes.storage.room.SongDao
import com.tunagold.oceantunes.storage.room.SongRoom
import com.tunagold.oceantunes.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SongRepository @Inject constructor(
    private val songDao: SongDao,
    private val lastFmRepository: ILastFmRepository
) : ISongRepository {

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
        return withContext(Dispatchers.IO) {
            try {
                val roomSong = songDao.getSongById(songId)
                if (roomSong != null) {
                    Result.Success(roomSong)
                } else {
                    val lastFmResult = lastFmRepository.getTrackInfoByMbid(songId)
                    when (lastFmResult) {
                        is Result.Success -> {
                            Result.Success(lastFmResult.data)
                        }
                        is Result.Error -> {
                            Result.Error(lastFmResult.exception)
                        }
                        Result.Loading -> Result.Error(Exception("Last.fm request is loading"))
                    }
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override fun getSongByIdFlow(songId: String): Result<Flow<SongRoom?>> {
        val flowResult = flow {
            emit(songDao.getSongByIdFlow(songId).firstOrNull())

            if (songDao.getSongById(songId) == null) {
                val lastFmResult = lastFmRepository.getTrackInfoByMbid(songId)
                if (lastFmResult is Result.Error) {
                    throw lastFmResult.exception
                }
            }
        }.catch { e ->
            emit(null)
            throw e
        }
        return Result.Success(flowResult)
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

    override fun getSongsByIds(songIds: List<String>): Flow<List<SongRoom>> {
        return flow {
            val initialRoomSongs = songDao.getSongsByIds(songIds).firstOrNull() ?: emptyList()
            emit(initialRoomSongs)

            val missingSongIds = songIds.filter { id -> initialRoomSongs.none { it.id == id } }

            if (missingSongIds.isNotEmpty()) {
                withContext(Dispatchers.IO) {
                    for (id in missingSongIds) {
                        lastFmRepository.getTrackInfoByMbid(id)
                    }
                }
            }
            songDao.getSongsByIds(songIds).collect(this)
        }.catch { e ->
            emit(emptyList())
        }
    }
}
