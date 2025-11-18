package com.phamthang.base.room_database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.phamthang.base.model.TypeWallpaperConverter
import com.phamthang.base.model.Wallpaper

@Database(entities = [Wallpaper::class], version = 1, exportSchema = false)
@TypeConverters(TypeWallpaperConverter::class)
abstract class WallpaperDatabase : RoomDatabase() {
    abstract fun dao(): WallpaperDao
}