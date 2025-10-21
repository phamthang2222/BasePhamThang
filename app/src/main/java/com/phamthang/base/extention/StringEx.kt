package com.phamthang.base.extention

import android.media.MediaMetadataRetriever
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
    return dateFormat.format(Date())
}
fun String.capitalizeFirst(): String =
    this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }


fun String.toMonthDay(): String {
    return try {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val date = inputFormat.parse(this)

        val outputFormat = SimpleDateFormat("MMM d", Locale.ENGLISH)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        this
    }
}

fun String.areDigitsOnly() = matches(Regex("[0-9]+"))

fun String.getInsideParentheses(): String {
    return "\\((.*?)\\)".toRegex().find(this)?.groups?.get(1)?.value ?: ""
}

fun String.getAudioFileDuration(): Long {
    if (!File(this).exists()) return 0L

    return try {
        if(isQPlus()){
            MediaMetadataRetriever().use { retriever ->
                retriever.setDataSource(this)
                val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                durationStr?.toLongOrNull() ?: 0L
            }
        }else{
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(this)
            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            retriever.release()
            durationStr?.toLongOrNull() ?: 0L
        }
    } catch (e: Exception) {
        e.printStackTrace()
        0L
    }
}

fun String.getNameFromUrl(): String {
    val fileName = this.substringAfterLast("/")
    return fileName
}
