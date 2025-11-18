package com.phamthang.base.repository

import com.phamthang.base.model.Wallpaper
import com.phamthang.base.room_database.WallpaperDao

class WallpaperRepository private constructor(private val wallpaperDao: WallpaperDao) {

    fun getAllWallpaper() = wallpaperDao.getAllWallpaper()

    suspend fun insertAllWallpaper(wallpapers: List<Wallpaper>) {
        wallpaperDao.insertAll(wallpapers)
    }

    suspend fun getThemeCount() : Int{
        return wallpaperDao.getThemeCount()
    }

    suspend fun unLockAllWallpaper(){
        wallpaperDao.unLockAllWallpaper()
    }

    suspend fun lockAllWallpaper(){
        wallpaperDao.lockAllWallpaper()
    }

    suspend fun unLockWallpaper(id: Int) {
        wallpaperDao.unLockWallpaper(id)
    }

    suspend fun unLockAllByCate(category: String) {
        wallpaperDao.unLockAllByCate(category)
    }

    suspend fun updateWallpaper(wallpaper: Wallpaper){
        wallpaperDao.update(wallpaper)
    }

    suspend fun getAllWallpaperList(): List<Wallpaper> {
        return wallpaperDao.getAllWallpaperList()
    }


    companion object {
        @Volatile
        private var INSTANCE: WallpaperRepository? = null

        fun getInstance(wallpaperDao: WallpaperDao): WallpaperRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = WallpaperRepository(wallpaperDao)
                INSTANCE = instance
                instance
            }
        }

    }

}