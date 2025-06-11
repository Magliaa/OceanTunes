package com.tunagold.oceantunes.ui.songsgrid

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunagold.oceantunes.model.GlobalSongStats
import com.tunagold.oceantunes.model.Song
import com.tunagold.oceantunes.model.UserSongInteraction
import com.tunagold.oceantunes.repository.user.IUserRepository
import com.tunagold.oceantunes.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongCardDialogViewModel @Inject constructor(
    private val userRepository: IUserRepository
) : ViewModel() {

    private val _song = MutableLiveData<Song>()
    val song: LiveData<Song> = _song

    private val _userInteraction = MutableLiveData<Result<UserSongInteraction?>>()
    val userInteraction: LiveData<Result<UserSongInteraction?>> = _userInteraction

    private val _globalStats = MutableLiveData<Result<GlobalSongStats?>>()
    val globalStats: LiveData<Result<GlobalSongStats?>> = _globalStats

    private val _favoriteStatus = MutableLiveData<Boolean>()
    val favoriteStatus: LiveData<Boolean> = _favoriteStatus

    private val _userRating = MutableLiveData<Float>()
    val userRating: LiveData<Float> = _userRating

    fun setSong(song: Song) {
        _song.value = song
        // Immediately fetch user-specific interaction and global stats for this song
        fetchUserInteraction(song.id)
        fetchGlobalSongStats(song.id)
    }

    private fun fetchUserInteraction(songId: String) {
        viewModelScope.launch {
            userRepository.getUserSongInteraction(songId).observeForever { result ->
                _userInteraction.value = result
                if (result is Result.Success) {
                    _favoriteStatus.value = result.data?.isFavorite ?: false
                    _userRating.value = result.data?.rating ?: 0.0f
                }
            }
        }
    }

    private fun fetchGlobalSongStats(songId: String) {
        viewModelScope.launch {
            userRepository.getGlobalSongStats(songId).observeForever { result ->
                _globalStats.value = result
            }
        }
    }

    fun toggleFavoriteStatus() {
        val currentSongId = _song.value?.id ?: return
        val currentFavoriteStatus = _favoriteStatus.value ?: false
        val newFavoriteStatus = !currentFavoriteStatus

        viewModelScope.launch {
            userRepository.setUserSongFavoriteStatus(currentSongId, newFavoriteStatus).observeForever { result ->
                if (result is Result.Success) {
                    _favoriteStatus.value = newFavoriteStatus
                    // Refresh global stats as favorite count might change
                    fetchGlobalSongStats(currentSongId)
                } else if (result is Result.Error) {
                    // Handle error (e.g., show toast)
                }
            }
        }
    }

    fun setUserRating(rating: Float) {
        val currentSongId = _song.value?.id ?: return
        viewModelScope.launch {
            userRepository.setUserSongRating(currentSongId, rating).observeForever { result ->
                if (result is Result.Success) {
                    _userRating.value = rating
                    // Refresh global stats as average rating might change
                    fetchGlobalSongStats(currentSongId)
                } else if (result is Result.Error) {
                    // Handle error (e.g., show toast)
                }
            }
        }
    }
    // You might also want a method to increment play count if applicable
    fun incrementPlayCount() {
        val currentSongId = _song.value?.id ?: return
        viewModelScope.launch {
            userRepository.incrementUserSongPlayCount(currentSongId).observeForever { result ->
                if (result is Result.Error) {
                    // Handle error (e.g., show toast)
                }
                // No need to update UI immediately for play count, as it's not directly displayed as current play count.
                // Global stats will reflect total play count eventually.
            }
        }
    }
}
