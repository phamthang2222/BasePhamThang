package com.phamthang.base.retrofit

import com.phamthang.base.model.Wallpaper
import retrofit2.http.GET

interface ApiService {
    @GET("api/APP7_WallpaperLauncher/all")
    suspend fun getAllWallpaper(): List<Wallpaper>
}