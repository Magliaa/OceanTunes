package com.tunagold.oceantunes.di

import com.tunagold.oceantunes.repository.song.ISongRepository
import com.tunagold.oceantunes.repository.song.SongRepository
import com.tunagold.oceantunes.repository.user.IUserRepository
import com.tunagold.oceantunes.repository.user.UserRepository
import com.tunagold.oceantunes.repository.images.IImageRepository // NUOVO IMPORTO
import com.tunagold.oceantunes.repository.images.ITunesImageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepository: UserRepository
    ): IUserRepository

    @Binds
    @Singleton
    abstract fun bindSongRepository(
        songRepository: SongRepository
    ): ISongRepository

    @Binds
    @Singleton
    abstract fun bindImageRepository(
        iTunesImageRepository: ITunesImageRepository
    ): IImageRepository
}