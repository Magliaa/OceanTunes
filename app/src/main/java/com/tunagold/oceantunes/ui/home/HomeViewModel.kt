package com.tunagold.oceantunes.ui.home

import android.util.Log
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

    private val _isLoadingTopFavoriteSongs = MutableLiveData<Boolean>(false)
    val isLoadingTopFavoriteSongs: LiveData<Boolean> = _isLoadingTopFavoriteSongs

    private val _isLoadingTopRatedSongs = MutableLiveData<Boolean>(false)
    val isLoadingTopRatedSongs: LiveData<Boolean> = _isLoadingTopRatedSongs

    init {
        Log.d("HomeViewModel", "ViewModel init block called, starting data fetches.")
        fetchTopFavoriteSongs()
        fetchTopRatedSongs()
    }

    fun fetchTopFavoriteSongs() {
        viewModelScope.launch {
            _isLoadingTopFavoriteSongs.value = true // Imposta lo stato di caricamento a true all'inizio
            Log.d("HomeViewModel", "Fetching top favorite songs... isLoadingTopFavoriteSongs: true")
            userRepository.getTopFavoriteSongs().observeForever { result -> // Changed parameter name to 'result'
                when (result) {
                    is Result.Loading -> {
                        // Non fare nulla qui per isLoading. La progress bar deve rimanere visibile.
                        Log.d("HomeViewModel", "Top favorite songs result: Loading")
                    }
                    is Result.Success -> {
                        _topFavoriteSongs.value = result // Aggiorna i dati
                        _isLoadingTopFavoriteSongs.value = false // Imposta lo stato di caricamento a false solo in caso di successo
                        Log.d("HomeViewModel", "Top favorite songs result: Success, size: ${result.data?.size ?: 0}, isLoadingTopFavoriteSongs: false")
                    }
                    is Result.Error -> {
                        _topFavoriteSongs.value = result // Aggiorna con l'errore
                        _isLoadingTopFavoriteSongs.value = false // Imposta lo stato di caricamento a false in caso di errore
                        Log.e("HomeViewModel", "Top favorite songs result: Error: ${result.exception?.message}, isLoadingTopFavoriteSongs: false")
                    }
                }
            }
        }
    }

    fun fetchTopRatedSongs() {
        viewModelScope.launch {
            _isLoadingTopRatedSongs.value = true // Imposta lo stato di caricamento a true all'inizio
            Log.d("HomeViewModel", "Fetching top rated songs... isLoadingTopRatedSongs: true")
            userRepository.getTopRatedSongs().observeForever { result -> // Changed parameter name to 'result'
                when (result) {
                    is Result.Loading -> {
                        // Non fare nulla qui per isLoading. La progress bar deve rimanere visibile.
                        Log.d("HomeViewModel", "Top rated songs result: Loading")
                    }
                    is Result.Success -> {
                        _topRatedSongs.value = result // Aggiorna i dati
                        _isLoadingTopRatedSongs.value = false // Imposta lo stato di caricamento a false solo in caso di successo
                        Log.d("HomeViewModel", "Top rated songs result: Success, size: ${result.data?.size ?: 0}, isLoadingTopRatedSongs: false")
                    }
                    is Result.Error -> {
                        _topRatedSongs.value = result // Aggiorna con l'errore
                        _isLoadingTopRatedSongs.value = false // Imposta lo stato di caricamento a false in caso di errore
                        Log.e("HomeViewModel", "Top rated songs result: Error: ${result.exception?.message}, isLoadingTopRatedSongs: false")
                    }
                }
            }
        }
    }
}
