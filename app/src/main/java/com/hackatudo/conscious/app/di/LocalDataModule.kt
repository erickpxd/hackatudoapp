package com.hackatudo.conscious.app.di

import android.content.Context
import androidx.room.Room
import com.hackatudo.conscious.core.database.AppDatabase
import com.hackatudo.conscious.core.time.AndroidClock
import com.hackatudo.conscious.core.time.AndroidMonotonicClock
import com.hackatudo.conscious.core.time.Clock
import com.hackatudo.conscious.core.time.MonotonicClock
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalDataBindings {
    @Binds abstract fun bindClock(implementation: AndroidClock): Clock
    @Binds abstract fun bindMonotonicClock(implementation: AndroidMonotonicClock): MonotonicClock
}

@Module
@InstallIn(SingletonComponent::class)
object LocalDataProviders {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "conscious-use.db").build()
}
