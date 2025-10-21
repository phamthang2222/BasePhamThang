package com.phamthang.base.extention

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.text.format.Formatter
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

const val SHARE_IMAGE = "image/*"
const val SHARE_VIDEO = "video/*"
const val SHARE_AUDIO = "audio/*"


fun File.shareFile(context: Context, type: String) {
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = type
    val uri = getUriFromFilePath(context)
    shareIntent.apply {
        putExtra(Intent.EXTRA_STREAM, uri)
        this.type = type
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    val chooser = Intent.createChooser(shareIntent, "Share")
    val resInfoList: List<ResolveInfo> = context.packageManager
        .queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)
    for (resolveInfo in resInfoList) {
        val packageName = resolveInfo.activityInfo.packageName
        context.grantUriPermission(
            packageName,
            uri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }

    if (shareIntent.resolveActivity(context.packageManager) != null) {
        if (context !is Activity) {
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    } else {
        Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show()
    }
}

private fun File.getUriFromFilePath(context: Context): Uri {
    return FileProvider.getUriForFile(context, context.packageName + ".provider", this)
}

fun File.getFileSize(context: Context): String {
    return Formatter.formatFileSize(context, this.length())
}

fun File.getVideoDurationInMillis(): Long {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(this.absolutePath)
        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        durationStr?.toLongOrNull() ?: 0L
    } catch (e: Exception) {
        e.printStackTrace()
        0L
    } finally {
        retriever.release()
    }
}

fun File.getLastModifiedTime(): Long {
    return this.lastModified()
}



