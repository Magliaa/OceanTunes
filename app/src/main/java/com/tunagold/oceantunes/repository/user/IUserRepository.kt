package com.tunagold.oceantunes.repository.user

import androidx.lifecycle.LiveData
import com.tunagold.oceantunes.model.Song
import com.tunagold.oceantunes.model.User
import com.tunagold.oceantunes.model.UserSongInteraction

interface IUserRepository {
    fun signIn(email: String, password: String): LiveData<Result<String>>
    fun signUp(email: String, password: String, username: String): LiveData<Result<String>>
    fun signInWithGoogle(idToken: String): LiveData<Result<String>>
    fun signOut(): LiveData<Result<String>>
    fun getCurrentUserUid(): String?
    fun updateUsername(newUsername: String): LiveData<Result<String>>
    fun getCurrentUserDetails(): LiveData<Result<User>>

    // Songs interaction data
    fun setUserSongRating(songId: String, rating: Int): LiveData<Result<Unit>>
    fun setUserSongFavoriteStatus(songId: String, isFavorite: Boolean): LiveData<Result<Unit>>
    fun incrementUserSongPlayCount(songId: String): LiveData<Result<Unit>>
    fun getUserSongInteraction(songId: String): LiveData<Result<UserSongInteraction?>>
    fun getFavoriteSongsForUser(): LiveData<Result<List<Song>>> // Returns full Song objects
}
