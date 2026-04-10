package com.phamthang.base.repository

import com.phamthang.base.model.Wallpaper
import kotlinx.coroutines.flow.Flow

interface  WallpaperRepository{
    suspend fun getAllWallpaper(): Flow<List<Wallpaper>>
    suspend fun insert(wallpaper: Wallpaper)
}