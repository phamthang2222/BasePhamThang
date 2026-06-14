package com.phamthang.base.data.mapper

import com.phamthang.base.domain.model.WallpaperDomain
import com.phamthang.base.model.Wallpaper

/**
 * Mapper chuyển đổi giữa Data Model (Entity/DTO) và Domain Model.
 * Việc sử dụng mapper giúp đảm bảo tính "sạch" (Clean) của kiến trúc.
 */
fun Wallpaper.toDomain(): WallpaperDomain {
    return WallpaperDomain(
        id = this.id,
        category = this.category,
        path = this.path,
        localPath = this.localPath,
        isDownloaded = this.state == Wallpaper.STATE_DOWNLOADED
    )
}

fun WallpaperDomain.toData(): Wallpaper {
    return Wallpaper(
        id = this.id,
        category = this.category,
        path = this.path,
        localPath = this.localPath,
        state = if (this.isDownloaded) Wallpaper.STATE_DOWNLOADED else Wallpaper.STATE_DEFAULT
    )
}
