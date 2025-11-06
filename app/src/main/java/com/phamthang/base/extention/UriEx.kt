package com.phamthang.base.extention

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File


fun Uri.toFile(context: Context): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(this) ?: return null
        val tempFile = File.createTempFile("temp_", null, context.cacheDir)
        tempFile.outputStream().use { output ->
            inputStream.copyTo(output)
        }
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun Uri.getFileName(context: Context): String?{
    var fileName: String? = null
    try {
        context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex)
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return fileName
}
