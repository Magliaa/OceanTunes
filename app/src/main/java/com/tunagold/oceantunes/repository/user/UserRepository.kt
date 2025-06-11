package com.tunagold.oceantunes.repository.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.FieldValue // Import FieldValue for increment
import com.tunagold.oceantunes.model.User
import com.tunagold.oceantunes.model.UserSongInteraction
import com.tunagold.oceantunes.model.Song
import com.tunagold.oceantunes.model.GlobalSongStats // Import GlobalSongStats
import com.tunagold.oceantunes.model.UserInteractionStats // Import UserInteractionStats
import com.tunagold.oceantunes.repository.song.ISongRepository // Import ISongRepository
import com.tunagold.oceantunes.utils.Result
import kotlinx.coroutines.flow.firstOrNull // Import firstOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await // Import await()
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore, // Injected
    private val songRepository: ISongRepository // Injected
) : IUserRepository {

    private val usersCollection = firestore.collection("users")
    private val globalSongsCollection = firestore.collection("globalSongs") // Collection for global song stats

    override fun signIn(email: String, password: String): LiveData<Result<String>> {
        val result = MutableLiveData<Result<String>>()
        result.value = Result.Loading // Set loading state
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
        result.value = Result.Loading // Set loading state
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
                                // Also create user document in Firestore
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
        result.value = Result.Loading // Set loading state
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    val uid = firebaseUser?.uid ?: ""
                    val email = firebaseUser?.email

                    // Check if user already exists in Firestore users collection
                    usersCollection.document(uid).get()
                        .addOnCompleteListener { getDocTask ->
                            if (getDocTask.isSuccessful && !getDocTask.result.exists()) {
                                // User does not exist, create new document
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
                                // User already exists
                                result.value = Result.Success("Signed in with Google")
                            } else {
                                // Error checking user existence
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
        result.value = Result.Loading // Set loading state
        auth.signOut()
        result.value = Result.Success("Signed out")
        return result
    }

    override fun getCurrentUserUid(): String? {
        return auth.currentUser?.uid
    }

    override fun updateUsername(newUsername: String): LiveData<Result<String>> {
        val result = MutableLiveData<Result<String>>()
        result.value = Result.Loading // Set loading state
        val firebaseUser = auth.currentUser

        if (firebaseUser != null) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newUsername)
                .build()

            firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        // Also update username in Firestore user document
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
        result.value = Result.Loading // Set loading state
        val firebaseUser = auth.currentUser

        if (firebaseUser != null) {
            usersCollection.document(firebaseUser.uid).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firestoreUser = task.result.toObject(User::class.java)
                        // Combine data from Firebase Auth and Firestore
                        if (firestoreUser != null) {
                            val fullUser = firestoreUser.copy(
                                uid = firebaseUser.uid,
                                email = firebaseUser.email,
                                displayName = firebaseUser.displayName ?: firestoreUser.displayName,
                                photoUrl = firebaseUser.photoUrl?.toString() ?: firestoreUser.photoUrl
                            )
                            result.value = Result.Success(fullUser)
                        } else {
                            // If Firestore doc doesn't exist, create a basic User from Auth data
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

                val oldRating = userSnapshot.getDouble("rating")?.toFloat() ?: 0f // Get previous rating
                val newRating = rating

                // Update user's specific song interaction
                transaction.set(userSongDocRef, mapOf("rating" to newRating, "songId" to songId, "userId" to uid), SetOptions.merge())

                // Update global song stats
                val globalSumOfRatings = globalSnapshot.getDouble("sumOfRatings") ?: 0.0
                val globalTotalRatedCount = globalSnapshot.getLong("totalRatedCount") ?: 0L

                val newSumOfRatings = if (oldRating > 0f && newRating > 0f) {
                    // User changed an existing rating
                    globalSumOfRatings - oldRating + newRating
                } else if (oldRating == 0f && newRating > 0f) {
                    // User rated for the first time
                    globalSumOfRatings + newRating
                } else if (oldRating > 0f && newRating == 0f) {
                    // User removed their rating (set to 0)
                    globalSumOfRatings - oldRating
                } else {
                    globalSumOfRatings // Rating remains 0 or invalid scenario
                }

                val newTotalRatedCount = if (oldRating == 0f && newRating > 0f) {
                    globalTotalRatedCount + 1 // New rating added
                } else if (oldRating > 0f && newRating == 0f) {
                    globalTotalRatedCount - 1 // Rating removed
                } else {
                    globalTotalRatedCount // Rating changed or no change from/to zero
                }

                val finalTotalRatedCount = maxOf(0L, newTotalRatedCount) // Ensure count doesn't go below zero

                transaction.set(globalSongStatsDocRef,
                    mapOf(
                        "id" to songId,
                        "sumOfRatings" to newSumOfRatings,
                        "totalRatedCount" to finalTotalRatedCount
                    ), SetOptions.merge()
                )
                null // Transaction must return null
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

                // Update user's specific song interaction
                transaction.set(userSongDocRef, mapOf("isFavorite" to isFavorite, "songId" to songId, "userId" to uid), SetOptions.merge())

                // Update global song stats if favorite status changed
                if (isFavorite != oldIsFavorite) {
                    val incrementValue = if (isFavorite) 1 else -1
                    transaction.set(globalSongStatsDocRef, mapOf("id" to songId, "totalFavoriteCount" to FieldValue.increment(incrementValue.toLong())), SetOptions.merge())
                }
                null // Transaction must return null
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

                // Increment global play count
                transaction.set(globalSongStatsDocRef, mapOf("id" to songId, "totalPlayCount" to FieldValue.increment(1)), SetOptions.merge())
                null // Transaction must return null
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

    private suspend fun fetchSongsFromRoom(songIds: List<String>): Result<List<Song>> {
        if (songIds.isEmpty()) return Result.Success(emptyList())

        return try {
            val songRooms = songRepository.getSongsByIds(songIds).firstOrNull() ?: emptyList()
            Result.Success(songRooms.map { it as Song })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getFavoriteSongsForUser(): LiveData<Result<List<Song>>> {
        val result = MutableLiveData<Result<List<Song>>>()
        result.value = Result.Loading
        val uid = auth.currentUser?.uid

        if (uid != null) {
            firestore.collection("users").document(uid)
                .collection("userSongs")
                .whereEqualTo("isFavorite", true)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val favoriteInteractionDocs = task.result.documents
                        val favoriteSongIds = favoriteInteractionDocs.mapNotNull { it.id }

                        CoroutineScope(Dispatchers.IO).launch {
                            val songsResult = fetchSongsFromRoom(favoriteSongIds)
                            result.postValue(songsResult)
                        }
                    } else {
                        result.value = Result.Error(task.exception ?: Exception("Failed to get favorite song interactions."))
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
            firestore.collection("users").document(uid)
                .collection("userSongs")
                .whereGreaterThan("rating", 0)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val ratedInteractionDocs = task.result.documents
                        val ratedSongIds = ratedInteractionDocs.mapNotNull { it.id }

                        CoroutineScope(Dispatchers.IO).launch {
                            val songsResult = fetchSongsFromRoom(ratedSongIds)
                            result.postValue(songsResult)
                        }
                    } else {
                        result.value = Result.Error(task.exception ?: Exception("Failed to get rated song interactions."))
                    }
                }
        } else {
            result.value = Result.Error(Exception("No user logged in to get rated songs."))
        }
        return result
    }

    // New: Get global statistics for a specific song
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

    // New: Get top favorite songs from global stats
    override fun getTopFavoriteSongs(): LiveData<Result<List<Song>>> {
        val result = MutableLiveData<Result<List<Song>>>()
        result.value = Result.Loading

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val globalStatsDocs = globalSongsCollection
                    .orderBy("totalFavoriteCount", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .limit(10) // Limit to top 10 for carousel
                    .get().await().documents

                val songIds = globalStatsDocs.mapNotNull { it.id }
                val songsResult = fetchSongsFromRoom(songIds)
                result.postValue(songsResult)
            } catch (e: Exception) {
                result.postValue(Result.Error(e))
            }
        }
        return result
    }

    // New: Get top rated songs from global stats
    override fun getTopRatedSongs(): LiveData<Result<List<Song>>> {
        val result = MutableLiveData<Result<List<Song>>>()
        result.value = Result.Loading

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val globalStatsDocs = globalSongsCollection
                    .orderBy("avgScore", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .limit(10) // Limit to top 10 for carousel
                    .get().await().documents

                val songIds = globalStatsDocs.mapNotNull { it.id }
                val songsResult = fetchSongsFromRoom(songIds)
                result.postValue(songsResult)
            } catch (e: Exception) {
                result.postValue(Result.Error(e))
            }
        }
        return result
    }
}
