package com.phamthang.base.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import kotlin.text.endsWith
import kotlin.text.lowercase

@Entity(tableName = "WALLPAPER_TABLE")
@Parcelize
data class Wallpaper(
    @PrimaryKey()
    val id: Int,
    val category: String,
    var path: String,
    var localPath: String? = null,
    var state: Int = STATE_DEFAULT,
    var typeFrom: TypeWallpaper = TypeWallpaper.FROM_SEVER,
    var isRewarded: Boolean = false
) : Parcelable {
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