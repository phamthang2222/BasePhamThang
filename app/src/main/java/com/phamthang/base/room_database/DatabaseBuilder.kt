package com.phamthang.base.room_database

import android.content.Context
import androidx.room.Room
import kotlin.jvm.java

object DatabaseBuilder {
    private var INSTANCE: WallpaperDatabase? = null

    fun getInstance(context: Context): WallpaperDatabase {
        if (INSTANCE == null) {
            synchronized(WallpaperDatabase::class) {
                if (INSTANCE == null) {
                    INSTANCE = buildRoomDB(context)
                }
            }
        }
        return INSTANCE!!
    }

    private fun buildRoomDB(context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            WallpaperDatabase::class.java,
            "WallpaperDatabase"
        )
            .fallbackToDestructiveMigration()
            .build()

}

/*
companion object {
    private var INSTANCE: WallpaperDatabase? = null
    fun getInstance(context: Context): WallpaperDatabase {
        if (INSTANCE == null) {
            synchronized(WallpaperDatabase::class) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    WallpaperDatabase::class.java,
                    "WallpaperDatabase"
                )
                    .allowMainThreadQueries()
                    .build()
            }
        }
        return INSTANCE!!
    }
}*/
