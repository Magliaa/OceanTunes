package com.tunagold.oceantunes.model

data class User(
    val uid: String = "",           // Fornisci un valore di default
    val email: String? = null,      // Già null, va bene
    val displayName: String? = null, // Già null, va bene
    val photoUrl: String? = null    // Già null, va bene
)