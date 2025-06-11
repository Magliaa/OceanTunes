package com.tunagold.oceantunes.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunagold.oceantunes.model.Song
import com.tunagold.oceantunes.repository.user.IUserRepository
import com.tunagold.oceantunes.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: IUserRepository
) : ViewModel() {

    private val _topFavoriteSongs = MutableLiveData<Result<List<Song>>>()
    val topFavoriteSongs: LiveData<Result<List<Song>>> = _topFavoriteSongs

    private val _topRatedSongs = MutableLiveData<Result<List<Song>>>()
    val topRatedSongs: LiveData<Result<List<Song>>> = _topRatedSongs

    init {
        fetchTopFavoriteSongs()
        fetchTopRatedSongs()
    }

    fun fetchTopFavoriteSongs() {
        viewModelScope.launch {
            userRepository.getTopFavoriteSongs().observeForever {
                _topFavoriteSongs.value = it
            }
        }
    }

    fun fetchTopRatedSongs() {
        viewModelScope.launch {
            userRepository.getTopRatedSongs().observeForever {
                _topRatedSongs.value = it
            }
        }
    }
}