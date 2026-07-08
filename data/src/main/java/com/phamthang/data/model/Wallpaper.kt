package com.phamthang.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TypeWallpaper {
    FROM_SEVER, FROM_DEVICE
}

@Entity(tableName = "WALLPAPER_TABLE")
data class Wallpaper(
    @PrimaryKey
    val id: Int,
    val category: String,
    val path: String,
    val localPath: String? = null,
    val state: Int = STATE_DEFAULT,
    val typeFrom: TypeWallpaper = TypeWallpaper.FROM_SEVER,
    val isRewarded: Boolean = false
) {
    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_DOWNLOADED = 1

        const val MP4 = 99981
        const val GIF = 33343
        const val PHOTO = 31456
    }

    fun getPathToUse(): String {
        return localPath ?: path
    }

    fun getCateName() :String {
        return  category.lowercase()
    }

    fun typeWallpaper(): Int {
        return if (path.endsWith(".mp4")) {
            MP4
        } else if (path.endsWith(".gif")) {
            GIF
        } else {
            PHOTO
        }
    }
}
