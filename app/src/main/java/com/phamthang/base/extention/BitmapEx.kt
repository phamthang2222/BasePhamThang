package com.phamthang.base.extention

import android.graphics.Bitmap
import android.os.Environment
import com.phamthang.base.extention.extention.logError
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Bitmap.saveBitmapToFile(): File? {
    return runCatching {
        val timestamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(Date())
        val fileName = "screenshot_$timestamp.png"

        val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val screenshotsDir = File(picturesDir, "Amazic/ScreenShot").apply {
            if (!exists()) mkdirs()
        }

        val file = File(screenshotsDir, fileName)
        FileOutputStream(file).use { out ->
            this.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

//        this.recycle()
        /* Handler(Looper.getMainLooper()).post {
             Toast.makeText(this, "Đã lưu: ${file.name}", Toast.LENGTH_SHORT).show()
         }*/
        file
    }.getOrElse { e ->
        e.printStackTrace()
        logError(tagSpecial = "saveBitmapToFile", message = "${e.printStackTrace()}" )
        null
    }
}