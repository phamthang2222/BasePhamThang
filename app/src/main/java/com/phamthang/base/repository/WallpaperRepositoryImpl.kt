package com.phamthang.base.repository

import com.phamthang.base.data.mapper.toData
import com.phamthang.base.data.mapper.toDomain
import com.phamthang.base.domain.model.WallpaperDomain
import com.phamthang.base.domain.repository.WallpaperRepository
import com.phamthang.base.room_database.WallpaperDao
import com.phamthang.base.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation của Repository nằm ở Data Layer.
 * Chịu trách nhiệm lấy dữ liệu từ các nguồn (DB, API) và map sang Domain Model.
 */
class WallpaperRepositoryImpl @Inject constructor(
    private val wallpaperDao: WallpaperDao
) : WallpaperRepository {

    override fun getAllWallpapers(): Flow<Resource<List<WallpaperDomain>>> {
        return wallpaperDao.getAllWallpaper().map { list ->
            Resource.Success(list.map { it.toDomain() })
        }
    }

    override suspend fun saveWallpaper(wallpaper: WallpaperDomain): Resource<Unit> {
        return try {
            wallpaperDao.insert(wallpaper.toData())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Database Error", e)
        }
    }
}
