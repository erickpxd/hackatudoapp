package com.hackatudo.conscious.app.di

import com.hackatudo.conscious.data.repository.RoomFocusContextRepository
import com.hackatudo.conscious.data.repository.RoomFocusSessionRepository
import com.hackatudo.conscious.domain.repository.FocusContextRepository
import com.hackatudo.conscious.domain.repository.FocusSessionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SessionModule {
    @Binds @Singleton abstract fun bindSessionRepository(implementation: RoomFocusSessionRepository): FocusSessionRepository
    @Binds @Singleton abstract fun bindContextRepository(implementation: RoomFocusContextRepository): FocusContextRepository
}
