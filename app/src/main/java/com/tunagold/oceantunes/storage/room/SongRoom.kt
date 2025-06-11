package com.tunagold.oceantunes.storage.room

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.TypeConverters
import com.tunagold.oceantunes.model.Song
import com.tunagold.oceantunes.storage.room.Converters.StringListConverter
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(primaryKeys = ["id"], tableName = "Song")
data class SongRoom (
    override val id: String,
    override val title: String,
    override val album: String,
    @TypeConverters(StringListConverter::class) override val artists: List<String>,
    override val image: String,
    override val releaseDate: String,
    @TypeConverters(StringListConverter::class) override val credits: List<String>
) : Song(id, title, artists, album, image, releaseDate, credits), Parcelable
