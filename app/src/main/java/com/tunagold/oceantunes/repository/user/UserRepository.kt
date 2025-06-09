package com.tunagold.oceantunes.repository.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.tunagold.oceantunes.model.User
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val auth: FirebaseAuth
) : IUserRepository {

    override fun signIn(email: String, password: String): LiveData<Result<String>> {
        val result = MutableLiveData<Result<String>>()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                result.value = if (task.isSuccessful) {
                    Result.success("Signed in successfully")
                } else {
                    Result.failure(task.exception ?: Exception("Unknown error"))
                }
            }
        return result
    }

    override fun signUp(email: String, password: String, username: String): LiveData<Result<String>> {
        val result = MutableLiveData<Result<String>>()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()

                    firebaseUser?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { updateTask ->
                            result.value = if (updateTask.isSuccessful) {
                                Result.success("Account created with username")
                            } else {
                                Result.failure(updateTask.exception ?: Exception("Failed to set username"))
                            }
                        }
                } else {
                    result.value = Result.failure(task.exception ?: Exception("Unknown error"))
                }
            }

        return result
    }

    override fun signInWithGoogle(idToken: String): LiveData<Result<String>> {
        val result = MutableLiveData<Result<String>>()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                result.value = if (task.isSuccessful) {
                    Result.success("Signed in with Google")
                } else {
                    Result.failure(task.exception ?: Exception("Google sign-in failed"))
                }
            }
        return result
    }

    override fun signOut(): LiveData<Result<String>> {
        val result = MutableLiveData<Result<String>>()
        auth.signOut()
        result.value = Result.success("Signed out")
        return result
    }

    override fun getCurrentUserUid(): String? {
        return auth.currentUser?.uid
    }

    // Updated: Update username directly on Firebase Auth profile
    override fun updateUsername(newUsername: String): LiveData<Result<String>> {
        val result = MutableLiveData<Result<String>>()
        val firebaseUser = auth.currentUser

        if (firebaseUser != null) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newUsername)
                .build()

            firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    result.value = if (task.isSuccessful) {
                        Result.success("Username updated successfully")
                    } else {
                        Result.failure(task.exception ?: Exception("Failed to update username"))
                    }
                }
        } else {
            result.value = Result.failure(Exception("No user is currently logged in."))
        }
        return result
    }

    override fun getCurrentUserDetails(): LiveData<Result<User>> {
        val result = MutableLiveData<Result<User>>()
        val firebaseUser = auth.currentUser

        if (firebaseUser != null) {
            val user = User(
                uid = firebaseUser.uid,
                email = firebaseUser.email,
                displayName = firebaseUser.displayName,
                photoUrl = firebaseUser.photoUrl?.toString()
            )
            result.value = Result.success(user)
        } else {
            result.value = Result.failure(Exception("No user is currently logged in."))
        }
        return result
    }
}
