package com.phamthang.base.retrofit

import com.phamthang.base.model.Wallpaper
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://207.148.116.90/"

    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    suspend fun getAllWallpaper(): List<Wallpaper> {
        return try {
            apiService.getAllWallpaper()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
