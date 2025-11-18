package com.phamthang.base.model

import androidx.room.TypeConverter

class TypeWallpaperConverter {

    @TypeConverter
    fun  fromTypeWallpaper(value: TypeWallpaper): String {
        return value.name
    }

    @TypeConverter
    fun toTypeWallpaper(value: String): TypeWallpaper {
        return try {
            TypeWallpaper.valueOf(value)
        } catch (e: IllegalArgumentException) {
            TypeWallpaper.FROM_SEVER
        }
    }

}