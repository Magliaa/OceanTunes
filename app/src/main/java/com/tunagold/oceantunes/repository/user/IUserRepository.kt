package com.tunagold.oceantunes.repository.user

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth

interface IUserRepository {
    fun getUser(email: String, password: String, isUserRegistered: Boolean): MutableLiveData<String>
    fun getGoogleUser(idToken: String): MutableLiveData<String>
    fun signOut(): MutableLiveData<String>
    fun signIn(email: String, password: String)
    fun signUp(auth: FirebaseAuth, email: String, password: String)
    fun resetPassword(auth: FirebaseAuth, email: String)
    fun updatePassword(auth: FirebaseAuth, password: String)
    fun signInWithGoogle(token: String)
}