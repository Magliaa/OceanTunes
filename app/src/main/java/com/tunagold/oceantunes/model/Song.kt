package com.tunagold.oceantunes.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class Song (
    open val id: String,
    open val title: String,
    open val artists: List<String>,
    open val album: String,
    open val image: String,
    open val releaseDate: String,
    open val credits: List<String>
): Parcelable