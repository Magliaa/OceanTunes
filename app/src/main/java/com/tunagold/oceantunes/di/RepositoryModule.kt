package com.tunagold.oceantunes.di

import com.tunagold.oceantunes.repository.song.ISongRepository
import com.tunagold.oceantunes.repository.song.SongRepository
import com.tunagold.oceantunes.repository.user.IUserRepository
import com.tunagold.oceantunes.repository.user.UserRepository
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
}