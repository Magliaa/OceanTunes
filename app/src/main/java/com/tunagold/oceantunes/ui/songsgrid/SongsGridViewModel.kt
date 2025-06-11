package com.tunagold.oceantunes.ui.songsgrid

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.tunagold.oceantunes.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SongsGridViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> = _songs

    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

    private val _isAscending = MutableLiveData<Boolean>(true)
    val isAscending: LiveData<Boolean> = _isAscending

    init {
        savedStateHandle.get<Array<Song>>("songs_list_key")?.let { songsArray ->
            _songs.value = songsArray.toList()
        }
        savedStateHandle.get<String>("title_key")?.let {
            _title.value = it
        }
    }

    fun updateSongs(newSongs: List<Song>) {
        _songs.value = newSongs
    }

    fun updateTitle(newTitle: String) {
        _title.value = newTitle
    }

    fun toggleSortOrder() {
        val currentOrder = _isAscending.value ?: true
        _isAscending.value = !currentOrder

        val currentSongs = _songs.value.orEmpty().toMutableList()
        if (currentOrder) {
            _songs.value = currentSongs.sortedByDescending { it.title }
        } else {
            _songs.value = currentSongs.sortedBy { it.title }
        }
    }
}