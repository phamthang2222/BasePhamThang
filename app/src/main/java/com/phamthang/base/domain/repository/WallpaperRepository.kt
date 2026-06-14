package com.phamthang.base.domain.repository

import com.phamthang.base.domain.model.WallpaperDomain
import com.phamthang.base.utils.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Interface Repository đặt tại Domain Layer.
 *
 */
interface WallpaperRepository {
    fun getAllWallpapers(): Flow<Resource<List<WallpaperDomain>>>
    suspend fun saveWallpaper(wallpaper: WallpaperDomain): Resource<Unit>
}
