package com.phamthang.reverseaudio.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audio_table")
data class AudioModel (
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val name : String,
    val pathOriginal : String,
    val pathReverse : String,
    val timeCreated : Long,
)