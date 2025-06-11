package com.tunagold.oceantunes.storage.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [SongRoom::class], version = 1)
@TypeConverters(Converters.StringListConverter::class)
abstract class SongDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao

    companion object {
        @Volatile
        private var Instance: SongDatabase? = null

        fun getDatabase(context: Context): SongDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, SongDatabase::class.java, "song_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
