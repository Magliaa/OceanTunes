package com.tunagold.oceantunes.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunagold.oceantunes.model.User
import com.tunagold.oceantunes.model.UserInteractionStats
import com.tunagold.oceantunes.model.Song
import com.tunagold.oceantunes.repository.user.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.tunagold.oceantunes.utils.Result

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: IUserRepository
) : ViewModel() {

    private val _currentUser = MutableLiveData<Result<User>>()
    val currentUser: LiveData<Result<User>> = _currentUser

    private val _updateUsernameResult = MutableLiveData<Result<String>>()
    val updateUsernameResult: LiveData<Result<String>> = _updateUsernameResult

    private val _signOutResult = MutableLiveData<Result<String>>()
    val signOutResult: LiveData<Result<String>> = _signOutResult

    private val _userInteractionStats = MutableLiveData<Result<UserInteractionStats>>()
    val userInteractionStats: LiveData<Result<UserInteractionStats>> = _userInteractionStats

    private val _favoriteSongs = MutableLiveData<Result<List<Song>>>()
    val favoriteSongs: LiveData<Result<List<Song>>> = _favoriteSongs

    private val _ratedSongs = MutableLiveData<Result<List<Song>>>()
    val ratedSongs: LiveData<Result<List<Song>>> = _ratedSongs

    private val _isLoadingFavoriteSongs = MutableLiveData<Boolean>(false)
    val isLoadingFavoriteSongs: LiveData<Boolean> = _isLoadingFavoriteSongs

    private val _isLoadingRatedSongs = MutableLiveData<Boolean>(false)
    val isLoadingRatedSongs: LiveData<Boolean> = _isLoadingRatedSongs

    fun fetchCurrentUserDetails() {
        _currentUser.value = Result.Loading
        viewModelScope.launch {
            userRepository.getCurrentUserDetails().observeForever { result ->
                _currentUser.value = result
            }
        }
    }

    fun fetchUserInteractionStats() {
        _userInteractionStats.value = Result.Loading
        viewModelScope.launch {
            userRepository.getUserInteractionStats().observeForever { result ->
                _userInteractionStats.value = result
            }
        }
    }

    fun fetchFavoriteSongs() {
        _isLoadingFavoriteSongs.value = true
        viewModelScope.launch {
            userRepository.getFavoriteSongsForUser().observeForever { result ->
                when (result) {
                    is Result.Loading -> {}
                    is Result.Success -> {
                        _favoriteSongs.value = result
                        _isLoadingFavoriteSongs.value = false
                    }
                    is Result.Error -> {
                        _favoriteSongs.value = result
                        _isLoadingFavoriteSongs.value = false
                    }
                }
            }
        }
    }

    fun fetchRatedSongs() {
        _isLoadingRatedSongs.value = true
        viewModelScope.launch {
            userRepository.getUserRatedSongs().observeForever { result ->
                when (result) {
                    is Result.Loading -> {}
                    is Result.Success -> {
                        _ratedSongs.value = result
                        _isLoadingRatedSongs.value = false
                    }
                    is Result.Error -> {
                        _ratedSongs.value = result
                        _isLoadingRatedSongs.value = false
                    }
                }
            }
        }
    }

    fun updateUsername(newUsername: String) {
        _updateUsernameResult.value = Result.Loading
        viewModelScope.launch {
            userRepository.updateUsername(newUsername).observeForever { result ->
                _updateUsernameResult.value = result
                if (result is Result.Success) {
                    fetchCurrentUserDetails()
                }
            }
        }
    }

    fun signOut() {
        _signOutResult.value = Result.Loading
        viewModelScope.launch {
            userRepository.signOut().observeForever { result ->
                _signOutResult.value = result
            }
        }
    }
}