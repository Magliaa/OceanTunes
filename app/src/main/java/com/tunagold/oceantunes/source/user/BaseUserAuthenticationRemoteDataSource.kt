package com.tunagold.oceantunes.source.user

import com.tunagold.oceantunes.model.User

abstract  class BaseUserAuthenticationRemoteDataSource {
    abstract fun getLoggedUser(): User
    abstract fun signIn(email: String, password: String)
    abstract fun signUp(email: String, password: String)
    abstract fun signInWithGoogle(token: String)
    abstract fun signOut()
}