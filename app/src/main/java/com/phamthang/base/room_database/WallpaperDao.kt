package com.phamthang.base.room_database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.phamthang.base.model.Wallpaper

@Dao
interface WallpaperDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(wallpapers: Wallpaper)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(wallpapers: List<Wallpaper>)

    //get
    @Query("SELECT * FROM WALLPAPER_TABLE")
    fun getAllWallpaper(): LiveData<List<Wallpaper>>

    @Query("SELECT * FROM WALLPAPER_TABLE")
    suspend fun getAllWallpaperList(): List<Wallpaper>

    @Query("SELECT * FROM WALLPAPER_TABLE WHERE category = :category")
    fun getThemeByCategory(category: String): LiveData<List<Wallpaper>>

    @Query("SELECT COUNT(id) FROM WALLPAPER_TABLE")
    suspend fun getThemeCount(): Int

    @Query("UPDATE WALLPAPER_TABLE SET state = :state WHERE id = :id")
    suspend fun setStateDownLoaded(id: Int, state: Int)

    @Update
    suspend fun update(themeModel: Wallpaper)

    //reward
    @Query("UPDATE WALLPAPER_TABLE SET isRewarded = 0 WHERE id = :id")
    suspend fun lockWallpaper(id: Int)

    @Query("UPDATE WALLPAPER_TABLE SET isRewarded = 1 WHERE id = :id")
    suspend fun unLockWallpaper(id: Int)

    @Query("UPDATE WALLPAPER_TABLE SET isRewarded = 1 WHERE isRewarded = 0 AND category = :category")
    suspend fun unLockAllByCate(category: String)

    @Query("UPDATE WALLPAPER_TABLE SET isRewarded = 1")
    suspend fun unLockAllWallpaper()

    @Query("UPDATE WALLPAPER_TABLE SET isRewarded = 0")
    suspend fun lockAllWallpaper()
}