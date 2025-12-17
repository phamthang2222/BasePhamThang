package com.phamthang.base.di

import android.content.Context
import androidx.room.Room
import com.phamthang.base.room_database.WallpaperDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext applicationContext: Context): WallpaperDatabase {
        return Room.databaseBuilder(
            applicationContext,
            WallpaperDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideNotificationDao(database: WallpaperDatabase) = database.dao()


}