package com.tunagold.oceantunes.model

data class User(
    val uid: String,
    val email: String?,
    val displayName: String?, // This will represent the username
    val photoUrl: String? = null
)