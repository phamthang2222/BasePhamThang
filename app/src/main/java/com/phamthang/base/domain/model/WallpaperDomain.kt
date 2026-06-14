package com.phamthang.base.domain.model

/**
 * Domain model cho Wallpaper.
 * Tách biệt hoàn toàn khỏi các annotations của Room hay Gson.
 * Giúp UI không bị phụ thuộc vào cấu trúc database/api.
 */
data class WallpaperDomain(
    val id: Int,
    val category: String,
    val path: String,
    val localPath: String? = null,
    val isDownloaded: Boolean = false
)
