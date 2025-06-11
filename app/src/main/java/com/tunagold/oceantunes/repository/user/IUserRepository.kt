package com.tunagold.oceantunes.repository.user

import androidx.lifecycle.LiveData
import com.tunagold.oceantunes.model.User
import com.tunagold.oceantunes.model.Song
import com.tunagold.oceantunes.model.UserSongInteraction
import com.tunagold.oceantunes.model.UserInteractionStats
import com.tunagold.oceantunes.model.GlobalSongStats // Import GlobalSongStats
import com.tunagold.oceantunes.utils.Result // Import your custom Result class

interface IUserRepository {
    fun signIn(email: String, password: String): LiveData<Result<String>>
    fun signUp(email: String, password: String, username: String): LiveData<Result<String>>
    fun signInWithGoogle(idToken: String): LiveData<Result<String>>
    fun signOut(): LiveData<Result<String>>
    fun getCurrentUserUid(): String?
    fun updateUsername(newUsername: String): LiveData<Result<String>>
    fun getCurrentUserDetails(): LiveData<Result<User>>

    fun setUserSongRating(songId: String, rating: Float): LiveData<Result<Unit>>
    fun setUserSongFavoriteStatus(songId: String, isFavorite: Boolean): LiveData<Result<Unit>>
    fun incrementUserSongPlayCount(songId: String): LiveData<Result<Unit>>
    fun getUserSongInteraction(songId: String): LiveData<Result<UserSongInteraction?>>

    fun getUserInteractionStats(): LiveData<Result<UserInteractionStats>>

    fun getFavoriteSongsForUser(): LiveData<Result<List<Song>>>
    fun getUserRatedSongs(): LiveData<Result<List<Song>>>

    fun getGlobalSongStats(songId: String): LiveData<Result<GlobalSongStats?>>

    fun getTopFavoriteSongs(): LiveData<Result<List<Song>>>
    fun getTopRatedSongs(): LiveData<Result<List<Song>>>
}
