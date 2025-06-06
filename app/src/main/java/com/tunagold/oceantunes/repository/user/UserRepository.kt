package com.tunagold.oceantunes.repository.user

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth

class UserRepository: IUserRepository {
    override fun getUser(
        email: String,
        password: String,
        isUserRegistered: Boolean
    ): MutableLiveData<String> {
        TODO("Not yet implemented")
    }

    override fun getGoogleUser(idToken: String): MutableLiveData<String> {
        TODO("Not yet implemented")
    }

    override fun signOut(): MutableLiveData<String> {
        TODO("Not yet implemented")
    }

    override fun signIn(email: String, password: String) {
        TODO("Not yet implemented")
    }

    override fun signUp(auth: FirebaseAuth, email: String, password: String) {
        TODO("Not yet implemented")
    }

    override fun resetPassword(auth: FirebaseAuth, email: String) {
        TODO("Not yet implemented")
    }

    override fun updatePassword(auth: FirebaseAuth, password: String) {
        TODO("Not yet implemented")
    }

    override fun signInWithGoogle(token: String) {
        TODO("Not yet implemented")
    }
}