package com.tunagold.oceantunes.model

open class User (
    open val email: String,
    open val firstName: String,
    open val lastName: String,
    open val rankNumber: Int,
    open val favNumber: Int,
    open val favSongs: List<String>,
    open val rankSongs: Map<String, Int>,
    open val favGenres: List<String>
)