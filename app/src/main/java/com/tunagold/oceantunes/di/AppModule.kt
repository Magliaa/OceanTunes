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
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import com.tunagold.oceantunes.repository.lastfm.ILastFmRepository
import com.tunagold.oceantunes.repository.lastfm.LastFmRepository
import com.tunagold.oceantunes.repository.song.ISongRepository
import com.tunagold.oceantunes.repository.song.SongRepository

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

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
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }
    }

    @Provides
    @Singleton
    fun provideLastFmApiKeyProvider(): () -> String {
        return {
            "ccfc7ef9d6630986d0d10e2d2c39972f"
        }
    }

    @Provides
    @Singleton
    fun provideLastFmRepository(
        httpClient: HttpClient,
        songDao: SongDao,
        apiKeyProvider: () -> String
    ): ILastFmRepository = LastFmRepository(httpClient, songDao, apiKeyProvider)

    @Provides
    fun provideGoogleAuthHelper(@ApplicationContext context: Context): GoogleAuthHelper {
        return GoogleAuthHelper(context)
    }
}
