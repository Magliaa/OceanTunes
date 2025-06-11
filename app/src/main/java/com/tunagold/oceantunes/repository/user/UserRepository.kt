package com.tunagold.oceantunes.repository.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.FieldValue
import com.tunagold.oceantunes.model.User
import com.tunagold.oceantunes.model.UserSongInteraction
import com.tunagold.oceantunes.model.Song
import com.tunagold.oceantunes.model.GlobalSongStats
import com.tunagold.oceantunes.model.UserInteractionStats
import com.tunagold.oceantunes.repository.song.ISongRepository
import com.tunagold.oceantunes.utils.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import android.util.Log
import com.tunagold.oceantunes.repository.lastfm.ILastFmRepository
import com.tunagold.oceantunes.model.toSong // Ensure this import is correct for SongRoom.toSong()

class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val songRepository: ISongRepository,
    private val lastFmRepository: ILastFmRepository // Ensure this is injected if not already
) : IUserRepository {

    private val usersCollection = firestore.collection("users")
    private val globalSongsCollection = firestore.collection("globalSongs")

    // --- Authentication and User Profile Methods (No change) ---
    override fun signIn(email: String, password: String): LiveData<Result<String>> {
        val result = MutableLiveData<Result<String>>()
        result.value = Result.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                result.value = if (task.isSuccessful) {
                    Result.Success("Signed in successfully")
                } else {
                    Result.Error(task.exception ?: Exception("Unknown error"))
                }
            }
        return result
    }

    override fun signUp(email: String, password: String, username: String): LiveData<Result<String>> {
        val result = MutableLiveData<Result<String>>()
        result.value = Result.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    val uid = firebaseUser?.uid ?: ""

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()

                    firebaseUser?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                val newUser = User(
                                    uid = uid,
                                    email = email,
                                    displayName = username,
                                    photoUrl = null
                                )
                                usersCollection.document(uid).set(newUser)
                                    .addOnCompleteListener { firestoreTask ->
                                        result.value = if (firestoreTask.isSuccessful) {
                                            Result.Success("Account created and profile initialized")
                                        } else {
                                            Result.Error(firestoreTask.exception ?: Exception("Failed to initialize user profile in Firestore"))
                                        }
                                    }
                            } else {
                                result.value = Result.Error(updateTask.exception ?: Exception("Failed to set username"))
                            }
                        }
                } else {
                    result.value = Result.Error(task.exception ?: Exception("Unknown error"))
                }
            }

        return result
    }

    override fun signInWithGoogle(idToken: String): LiveData<Result<String>> {
        val result = MutableLiveData<Result<String>>()
        result.value = Result.Loading
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    val uid = firebaseUser?.uid ?: ""
                    val email = firebaseUser?.email

                    usersCollection.document(uid).get()
                        .addOnCompleteListener { getDocTask ->
                            if (getDocTask.isSuccessful && !getDocTask.result.exists()) {
                                val newUser = User(
                                    uid = uid,
                                    email = email,
                                    displayName = firebaseUser?.displayName,
                                    photoUrl = firebaseUser?.photoUrl?.toString()
                                )
                                usersCollection.document(uid).set(newUser)
                                    .addOnCompleteListener { setDocTask ->
                                        result.value = if (setDocTask.isSuccessful) {
                                            Result.Success("Signed in with Google and profile initialized")
                                        } else {
                                            Result.Error(setDocTask.exception ?: Exception("Failed to initialize Google user profile in Firestore"))
                                        }
                                    }
                            } else if (getDocTask.isSuccessful) {
                                result.value = Result.Success("Signed in with Google")
                            } else {
                                result.value = Result.Error(getDocTask.exception ?: Exception("Failed to check Google user profile existence"))
                            }
                        }
                } else {
                    result.value = Result.Error(task.exception ?: Exception("Google sign-in failed"))
                }
            }
        return result
    }

    override fun signOut(): LiveData<Result<String>> {
        val result = MutableLiveData<Result<String>>()
        result.value = Result.Loading
        auth.signOut()
        result.value = Result.Success("Signed out")
        return result
    }

    override fun getCurrentUserUid(): String? {
        return auth.currentUser?.uid
    }

    override fun updateUsername(newUsername: String): LiveData<Result<String>> {
        val result = MutableLiveData<Result<String>>()
        result.value = Result.Loading
        val firebaseUser = auth.currentUser

        if (firebaseUser != null) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newUsername)
                .build()

            firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        val updates = mapOf("displayName" to newUsername)
                        usersCollection.document(firebaseUser.uid).update(updates)
                            .addOnCompleteListener { firestoreTask ->
                                result.value = if (firestoreTask.isSuccessful) {
                                    Result.Success("Username updated successfully")
                                } else {
                                    Result.Error(firestoreTask.exception ?: Exception("Failed to update username in Firestore"))
                                }
                            }
                    } else {
                        result.value = Result.Error(authTask.exception ?: Exception("Failed to update username in Firebase Auth"))
                    }
                }
        } else {
            result.value = Result.Error(Exception("No user is currently logged in."))
        }
        return result
    }

    override fun getCurrentUserDetails(): LiveData<Result<User>> {
        val result = MutableLiveData<Result<User>>()
        result.value = Result.Loading
        val firebaseUser = auth.currentUser

        if (firebaseUser != null) {
            usersCollection.document(firebaseUser.uid).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firestoreUser = task.result.toObject(User::class.java)
                        if (firestoreUser != null) {
                            val fullUser = firestoreUser.copy(
                                uid = firebaseUser.uid,
                                email = firebaseUser.email,
                                displayName = firebaseUser.displayName ?: firestoreUser.displayName,
                                photoUrl = firebaseUser.photoUrl?.toString() ?: firestoreUser.photoUrl
                            )
                            result.value = Result.Success(fullUser)
                        } else {
                            val basicUser = User(
                                uid = firebaseUser.uid,
                                email = firebaseUser.email,
                                displayName = firebaseUser.displayName,
                                photoUrl = firebaseUser.photoUrl?.toString()
                            )
                            result.value = Result.Success(basicUser)
                        }
                    } else {
                        result.value = Result.Error(task.exception ?: Exception("Failed to get user profile from Firestore."))
                    }
                }
        } else {
            result.value = Result.Error(Exception("No user is currently logged in."))
        }
        return result
    }

    // --- Song Interaction and Stats Methods (No change) ---
    override fun setUserSongRating(songId: String, rating: Float): LiveData<Result<Unit>> {
        val result = MutableLiveData<Result<Unit>>()
        result.value = Result.Loading
        val uid = auth.currentUser?.uid

        if (uid != null) {
            val userSongDocRef = firestore.collection("users").document(uid).collection("userSongs").document(songId)
            val globalSongStatsDocRef = globalSongsCollection.document(songId)

            firestore.runTransaction { transaction ->
                val userSnapshot = transaction.get(userSongDocRef)
                val globalSnapshot = transaction.get(globalSongStatsDocRef)

                val oldRating = userSnapshot.getDouble("rating")?.toFloat() ?: 0f
                val newRating = rating

                transaction.set(userSongDocRef, mapOf("rating" to newRating, "songId" to songId, "userId" to uid), SetOptions.merge())

                val globalSumOfRatings = globalSnapshot.getDouble("sumOfRatings") ?: 0.0
                val globalTotalRatedCount = globalSnapshot.getLong("totalRatedCount") ?: 0L

                val newSumOfRatings = if (oldRating > 0f && newRating > 0f) {
                    globalSumOfRatings - oldRating + newRating
                } else if (oldRating == 0f && newRating > 0f) {
                    globalSumOfRatings + newRating
                } else if (oldRating > 0f && newRating == 0f) {
                    globalSumOfRatings - oldRating
                } else {
                    globalSumOfRatings
                }

                val newTotalRatedCount = if (oldRating == 0f && newRating > 0f) {
                    globalTotalRatedCount + 1
                } else if (oldRating > 0f && newRating == 0f) {
                    globalTotalRatedCount - 1
                } else {
                    globalTotalRatedCount
                }

                val finalTotalRatedCount = maxOf(0L, newTotalRatedCount)

                transaction.set(globalSongStatsDocRef,
                    mapOf(
                        "id" to songId,
                        "sumOfRatings" to newSumOfRatings,
                        "totalRatedCount" to finalTotalRatedCount
                    ), SetOptions.merge()
                )
                null
            }.addOnSuccessListener { result.value = Result.Success(Unit) }
                .addOnFailureListener { e -> result.value = Result.Error(e) }
        } else {
            result.value = Result.Error(Exception("No user logged in to rate song."))
        }
        return result
    }

    override fun setUserSongFavoriteStatus(songId: String, isFavorite: Boolean): LiveData<Result<Unit>> {
        val result = MutableLiveData<Result<Unit>>()
        result.value = Result.Loading
        val uid = auth.currentUser?.uid

        if (uid != null) {
            val userSongDocRef = firestore.collection("users").document(uid).collection("userSongs").document(songId)
            val globalSongStatsDocRef = globalSongsCollection.document(songId)

            firestore.runTransaction { transaction ->
                val userSnapshot = transaction.get(userSongDocRef)
                val globalSnapshot = transaction.get(globalSongStatsDocRef)

                val oldIsFavorite = userSnapshot.getBoolean("isFavorite") ?: false

                transaction.set(userSongDocRef, mapOf("isFavorite" to isFavorite, "songId" to songId, "userId" to uid), SetOptions.merge())

                if (isFavorite != oldIsFavorite) {
                    val incrementValue = if (isFavorite) 1 else -1
                    transaction.set(globalSongStatsDocRef, mapOf("id" to songId, "totalFavoriteCount" to FieldValue.increment(incrementValue.toLong())), SetOptions.merge())
                }
                null
            }.addOnSuccessListener { result.value = Result.Success(Unit) }
                .addOnFailureListener { e -> result.value = Result.Error(e) }
        } else {
            result.value = Result.Error(Exception("No user logged in to favorite song."))
        }
        return result
    }

    override fun incrementUserSongPlayCount(songId: String): LiveData<Result<Unit>> {
        val result = MutableLiveData<Result<Unit>>()
        result.value = Result.Loading
        val uid = auth.currentUser?.uid

        if (uid != null) {
            val userSongDocRef = firestore.collection("users").document(uid).collection("userSongs").document(songId)
            val globalSongStatsDocRef = globalSongsCollection.document(songId)

            firestore.runTransaction { transaction ->
                val userSnapshot = transaction.get(userSongDocRef)
                val globalSnapshot = transaction.get(globalSongStatsDocRef)

                val currentPlayCount = userSnapshot.getLong("playCount") ?: 0
                val newPlayCount = currentPlayCount + 1
                val updates = hashMapOf<String, Any>(
                    "playCount" to newPlayCount,
                    "lastPlayed" to System.currentTimeMillis()
                )
                transaction.set(userSongDocRef, updates, SetOptions.merge())

                transaction.set(globalSongStatsDocRef, mapOf("id" to songId, "totalPlayCount" to FieldValue.increment(1)), SetOptions.merge())
                null
            }.addOnSuccessListener { result.value = Result.Success(Unit) }
                .addOnFailureListener { e -> result.value = Result.Error(e) }
        } else {
            result.value = Result.Error(Exception("No user logged in to track play count."))
        }
        return result
    }

    override fun getUserSongInteraction(songId: String): LiveData<Result<UserSongInteraction?>> {
        val result = MutableLiveData<Result<UserSongInteraction?>>()
        result.value = Result.Loading
        val uid = auth.currentUser?.uid

        if (uid != null) {
            firestore.collection("users").document(uid)
                .collection("userSongs").document(songId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val interaction = task.result.toObject(UserSongInteraction::class.java)
                        result.value = Result.Success(interaction)
                    } else {
                        result.value = Result.Error(task.exception ?: Exception("Failed to get song interaction."))
                    }
                }
        } else {
            result.value = Result.Error(Exception("No user logged in to get song interaction."))
        }
        return result
    }

    override fun getUserInteractionStats(): LiveData<Result<UserInteractionStats>> {
        val result = MutableLiveData<Result<UserInteractionStats>>()
        result.value = Result.Loading
        val uid = auth.currentUser?.uid

        if (uid != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val userSongsCollection = firestore.collection("users").document(uid).collection("userSongs")

                    val favoriteCount = userSongsCollection.whereEqualTo("isFavorite", true).get().await().size().toLong()
                    val ratedCount = userSongsCollection.whereGreaterThan("rating", 0).get().await().size().toLong()

                    result.postValue(Result.Success(UserInteractionStats(totalFavoriteSongs = favoriteCount, totalRatedSongs = ratedCount)))
                } catch (e: Exception) {
                    result.postValue(Result.Error(e))
                }
            }
        } else {
            result.value = Result.Error(Exception("No user logged in to get interaction stats."))
        }
        return result
    }

    // --- NEW / MODIFIED SONG RETRIEVAL LOGIC ---

    // This private function now correctly leverages SongRepository.getSongById
    private suspend fun fetchAndCacheSong(songId: String): Song? {
        val result = songRepository.getSongById(songId) // This returns Result<SongRoom?>

        return when (result) {
            is Result.Success -> {
                // If song is found (or fetched and cached by SongRepository), convert it to domain Song
                result.data?.toSong()
            }
            is Result.Error -> {
                Log.e("UserRepository", "Failed to fetch/cache song $songId: ${result.exception.message}")
                null // Return null on error
            }
            Result.Loading -> {
                // This state should ideally not be reached for a suspend function returning Result directly
                Log.w("UserRepository", "Unexpected Loading state for suspend call to getSongById: $songId")
                null
            }
        }
    }

    // --- Modified methods to use fetchAndCacheSong ---

    override fun getFavoriteSongsForUser(): LiveData<Result<List<Song>>> {
        val result = MutableLiveData<Result<List<Song>>>()
        result.value = Result.Loading
        val uid = auth.currentUser?.uid

        if (uid != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val favoriteInteractionDocs = firestore.collection("users").document(uid)
                        .collection("userSongs")
                        .whereEqualTo("isFavorite", true)
                        .get().await().documents

                    val favoriteSongIds = favoriteInteractionDocs.mapNotNull { it.id }
                    val songs = mutableListOf<Song>()

                    for (songId in favoriteSongIds) {
                        fetchAndCacheSong(songId)?.let { song ->
                            songs.add(song)
                        }
                    }
                    result.postValue(Result.Success(songs))
                } catch (e: Exception) {
                    Log.e("UserRepository", "Error getting favorite songs: ${e.message}", e)
                    result.postValue(Result.Error(e))
                }
            }
        } else {
            result.value = Result.Error(Exception("No user logged in to get favorite songs."))
        }
        return result
    }

    override fun getUserRatedSongs(): LiveData<Result<List<Song>>> {
        val result = MutableLiveData<Result<List<Song>>>()
        result.value = Result.Loading
        val uid = auth.currentUser?.uid

        if (uid != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val ratedInteractionDocs = firestore.collection("users").document(uid)
                        .collection("userSongs")
                        .whereGreaterThan("rating", 0)
                        .get().await().documents

                    val ratedSongIds = ratedInteractionDocs.mapNotNull { it.id }
                    val songs = mutableListOf<Song>()

                    for (songId in ratedSongIds) {
                        fetchAndCacheSong(songId)?.let { song ->
                            songs.add(song)
                        }
                    }
                    result.postValue(Result.Success(songs))
                } catch (e: Exception) {
                    Log.e("UserRepository", "Error getting rated songs: ${e.message}", e)
                    result.postValue(Result.Error(e))
                }
            }
        } else {
            result.value = Result.Error(Exception("No user logged in to get rated songs."))
        }
        return result
    }

    override fun getGlobalSongStats(songId: String): LiveData<Result<GlobalSongStats?>> {
        val result = MutableLiveData<Result<GlobalSongStats?>>()
        result.value = Result.Loading

        globalSongsCollection.document(songId).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val globalStats = task.result.toObject(GlobalSongStats::class.java)
                    result.value = Result.Success(globalStats)
                } else {
                    result.value = Result.Error(task.exception ?: Exception("Failed to get global song stats for $songId"))
                }
            }
        return result
    }

    override fun getTopFavoriteSongs(): LiveData<Result<List<Song>>> {
        val result = MutableLiveData<Result<List<Song>>>()
        result.value = Result.Loading

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val globalStatsDocs = globalSongsCollection
                    .orderBy("totalFavoriteCount", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .limit(25)
                    .get().await().documents

                val songIds = globalStatsDocs.mapNotNull { it.id }
                val songs = mutableListOf<Song>()

                for (songId in songIds) {
                    fetchAndCacheSong(songId)?.let { song ->
                        songs.add(song)
                    }
                }
                result.postValue(Result.Success(songs))
            } catch (e: Exception) {
                Log.e("UserRepository", "Error getting top favorite songs: ${e.message}", e)
                result.postValue(Result.Error(e))
            }
        }
        return result
    }

    override fun getTopRatedSongs(): LiveData<Result<List<Song>>> {
        val result = MutableLiveData<Result<List<Song>>>()
        result.value = Result.Loading

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val globalStatsDocs = globalSongsCollection
                    .orderBy("totalRatedCount", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .limit(25)
                    .get().await().documents

                val songIds = globalStatsDocs.mapNotNull { it.id }
                val songs = mutableListOf<Song>()

                for (songId in songIds) {
                    fetchAndCacheSong(songId)?.let { song ->
                        songs.add(song)
                    }
                }
                result.postValue(Result.Success(songs))
            } catch (e: Exception) {
                Log.e("UserRepository", "Error getting top rated songs: ${e.message}", e)
                result.postValue(Result.Error(e))
            }
        }
        return result
    }
}