package com.tunagold.oceantunes.model

open class Song (
    open val id: String,
    open val title: String,
    open val artists: List<String>,
    open val album: String,
    open val image: String,
    open val releaseDate: String,
    open val credits: List<String>,
    open val ranking: Int,
    open val avgScore: Float,
    open val favNumber: Int,
    open val ratersNumber: Int
)