package com.phamthang.reverseaudio.extention

import android.util.Log

fun Any.logDebug(tagSpecial: String = "", message: Any) {
    val tag = "Checkvar" + this::class.java.simpleName
    Log.d(tag, if (tagSpecial.isNotEmpty()) "$tagSpecial:\n$message" else message.toString())
}

fun Any.logError(tagSpecial: String = "", message: String, throwable: Throwable? = null) {
    val tag = "Checkvar" + this::class.java.simpleName
    Log.e(tag, if (tagSpecial.isNotEmpty()) "$tagSpecial:\n$message" else message, throwable)
}
