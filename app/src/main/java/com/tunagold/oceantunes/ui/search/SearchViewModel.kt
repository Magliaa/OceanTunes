package com.tunagold.oceantunes.ui.search

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunagold.oceantunes.model.Song
import com.tunagold.oceantunes.repository.lastfm.ILastFmRepository
import com.tunagold.oceantunes.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import com.tunagold.oceantunes.R
import android.util.Log

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val lastFmRepository: ILastFmRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _searchResults = MutableLiveData<Result<List<Song>>>()
    val searchResults: LiveData<Result<List<Song>>> = _searchResults

    private val _recentSearches = MutableLiveData<List<Triple<String, String, String>>>()
    val recentSearches: LiveData<List<Triple<String, String, String>>> = _recentSearches

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _noResultsMessage = MutableLiveData<String>()
    val noResultsMessage: LiveData<String> = _noResultsMessage

    private var searchJob: Job? = null
    private val prefsKey = "recent_searches"
    private val recentSearchesMaxCount = 10

    init {
        loadRecentSearches()
    }

    fun searchSongs(query: String) {
        searchJob?.cancel()

        if (query.isEmpty()) {
            Log.d("SearchViewModel", "Search query is empty, showing recent searches.")
            _noResultsMessage.value = "Nessuna ricerca recente"
            _searchResults.value = Result.Success(emptyList())
            _isLoading.value = false
            return
        }

        _isLoading.value = true
        _noResultsMessage.value = ""
        Log.d("SearchViewModel", "Initiating search for query: $query")

        searchJob = viewModelScope.launch {
            delay(300)

            val result = lastFmRepository.searchTracks(query)
            _searchResults.value = result

            when (result) {
                is Result.Success -> {
                    Log.d("SearchViewModel", "Search successful. ${result.data?.size} songs found.")
                    if (result.data.isNullOrEmpty()) {
                        _noResultsMessage.value = "Nessun risultato trovato"
                    } else {
                        _noResultsMessage.value = ""
                    }
                }
                is Result.Error -> {
                    Log.e("SearchViewModel", "Search failed: ${result.exception.message}", result.exception)
                    _noResultsMessage.value = "Errore durante la ricerca: ${result.exception.message}"
                }
                Result.Loading -> { }
            }
            _isLoading.value = false
        }
    }

    fun addRecentSearch(songData: Triple<String, String, String>) {
        val currentList = _recentSearches.value.orEmpty().toMutableList()
        currentList.removeAll { it.first == songData.first && it.second == songData.second && it.third == songData.third }
        currentList.add(0, songData)
        if (currentList.size > recentSearchesMaxCount) {
            currentList.removeLast()
        }
        _recentSearches.value = currentList
        saveRecentSearches(currentList)
        Log.d("SearchViewModel", "Added recent search: ${songData.first} by ${songData.second}. Current recent searches: ${currentList.size}")
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
        saveRecentSearches(emptyList())
        _noResultsMessage.value = "Nessuna ricerca recente"
        _searchResults.value = Result.Success(emptyList())
        Log.d("SearchViewModel", "Recent searches cleared.")
    }

    private fun loadRecentSearches() {
        val prefs = context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
        val strings = prefs.getStringSet(prefsKey, emptySet()) ?: emptySet()
        val loadedList = strings.mapNotNull { line ->
            val parts = line.split("|")
            if (parts.size == 3) {
                Triple(parts[0], parts[1], parts[2])
            } else null
        }.toMutableList()
        _recentSearches.value = loadedList
        if (loadedList.isEmpty() && _noResultsMessage.value.isNullOrEmpty()) {
            _noResultsMessage.value = "Nessuna ricerca recente"
        }
        Log.d("SearchViewModel", "Loaded ${loadedList.size} recent searches.")
    }

    private fun saveRecentSearches(list: List<Triple<String, String, String>>) {
        val prefs = context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
        val strings = list.map { "${it.first}|${it.second}|${it.third}" }.toSet()
        prefs.edit {
            putStringSet(prefsKey, strings)
        }
        Log.d("SearchViewModel", "Saved ${list.size} recent searches.")
    }
}