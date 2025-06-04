package com.tunagold.oceantunes.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tunagold.oceantunes.repository.song.SongRepository

class HomeViewModelFactory(
    private val repository: SongRepository
) : ViewModelProvider.Factory {

   /* override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    } */

}
