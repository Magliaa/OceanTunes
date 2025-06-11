package com.tunagold.oceantunes.model

import com.tunagold.oceantunes.storage.room.SongRoom

fun SongRoom.toSong(): Song {
    return Song(
        id = this.id,
        title = this.title,
        artists = this.artists,
        album = this.album,
        releaseDate = this.releaseDate,
        image = this.image,
        credits = this.credits,
    )
}