package com.phamthang.base.repository

import com.phamthang.base.model.Wallpaper
import com.phamthang.base.room_database.WallpaperDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WallpaperRepositoryImpl @Inject constructor(
    private val wallpaperDao: WallpaperDao
) : WallpaperRepository {

    override suspend fun getAllWallpaper(): Flow<List<Wallpaper>> {
       return  wallpaperDao.getAllWallpaper()
    }

    override suspend fun insert(wallpaper: Wallpaper) {
        wallpaperDao.insert(wallpaper)
    }

}