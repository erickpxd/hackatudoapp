package com.hackatudo.conscious.app.di

import com.hackatudo.conscious.data.repository.RoomFocusContextRepository
import com.hackatudo.conscious.data.repository.RoomFocusSessionRepository
import com.hackatudo.conscious.domain.repository.FocusContextRepository
import com.hackatudo.conscious.domain.repository.FocusSessionRepository
import com.hackatudo.conscious.data.repository.RoomSessionEventRepository
import com.hackatudo.conscious.domain.repository.SessionEventRepository
import com.hackatudo.conscious.data.repository.RoomSessionSummaryRepository
import com.hackatudo.conscious.domain.repository.SessionSummaryRepository
import com.hackatudo.conscious.data.repository.RoomStudyGroupRepository
import com.hackatudo.conscious.domain.repository.StudyGroupRepository
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
    @Binds @Singleton abstract fun bindSessionEventRepository(implementation: RoomSessionEventRepository): SessionEventRepository
    @Binds @Singleton abstract fun bindSessionSummaryRepository(implementation: RoomSessionSummaryRepository): SessionSummaryRepository
    @Binds @Singleton abstract fun bindStudyGroupRepository(implementation: RoomStudyGroupRepository): StudyGroupRepository
}
