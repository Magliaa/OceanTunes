package com.tunagold.oceantunes.storage.room

import androidx.room.Entity
import androidx.room.TypeConverters
import com.tunagold.oceantunes.model.Song
import com.tunagold.oceantunes.storage.room.Converters.StringListConverter

@Entity(primaryKeys = ["id"], tableName = "Song")
data class SongRoom (
    override val id: String,
    override val title: String,
    override val album: String,
    @TypeConverters(StringListConverter::class) override val artists: List<String>,
    override val image: String,
    override val releaseDate: String,
    @TypeConverters(StringListConverter::class) override val credits: List<String>,
    override val ranking: Int,
    override val avgScore: Float,
    override val favNumber: Int,
    override val ratersNumber: Int
) : Song(id, title, artists, album, image, releaseDate, credits, ranking, avgScore, favNumber, ratersNumber)
