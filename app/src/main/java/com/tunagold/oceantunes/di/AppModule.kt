package com.tunagold.oceantunes.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tunagold.oceantunes.storage.room.SongDao
import com.tunagold.oceantunes.storage.room.SongDatabase
import com.tunagold.oceantunes.utils.GoogleAuthHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // Firebase
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    // Firestore
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()


    // Room
    @Provides
    @Singleton
    fun provideSongDatabase(@ApplicationContext context: Context): SongDatabase =
        Room.databaseBuilder(
            context,
            SongDatabase::class.java,
            "song_database"
        ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideSongDao(db: SongDatabase): SongDao = db.songDao()

    @Provides
    fun provideGoogleAuthHelper(@ApplicationContext context: Context): GoogleAuthHelper {
        return GoogleAuthHelper(context)
    }
}