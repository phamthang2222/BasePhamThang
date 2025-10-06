package com.phamthang.reverseaudio.extention

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.text.format.Time
import java.util.Calendar
import java.util.Locale

fun Context.getTimeFormat() = "HH:mm"


fun Int.getContrastColor(): Int {
    val y = (299 * Color.red(this) + 587 * Color.green(this) + 114 * Color.blue(this)) / 1000
    return if (y >= 149 && this != Color.BLACK) Color.GREEN else Color.WHITE
}

@SuppressLint("DefaultLocale")
fun Int.toHex() = String.format("#%06X", 0xFFFFFF and this).uppercase(Locale.getDefault())

fun Int.adjustAlpha(factor: Float): Int {
    val alpha = Math.round(Color.alpha(this) * factor)
    val red = Color.red(this)
    val green = Color.green(this)
    val blue = Color.blue(this)
    return Color.argb(alpha, red, green, blue)
}

fun Int.isThisYear(): Boolean {
    val time = Time()
    time.set(this * 1000L)

    val thenYear = time.year
    time.set(System.currentTimeMillis())

    return (thenYear == time.year)
}

fun Int.toHourMinuteString(context: Context): String {
    val cal = Calendar.getInstance()
    cal.timeInMillis = this * 1000L
    return DateFormat.format(context.getTimeFormat(), cal).toString()
}

fun Int.getFormattedDuration(forceShowHours: Boolean = false): String {
    val sb = StringBuilder(8)
    val hours = this / 3600
    val minutes = this % 3600 / 60
    val seconds = this % 60

    if (this >= 3600) {
        sb.append(String.format(Locale.getDefault(), "%02d", hours)).append(":")
    } else if (forceShowHours) {
        sb.append("0:")
    }

    sb.append(String.format(Locale.getDefault(), "%02d", minutes))
    sb.append(":").append(String.format(Locale.getDefault(), "%02d", seconds))
    return sb.toString()
}

fun Int.formatDateOrTime(
    context: Context,
    hideTimeAtOtherDays: Boolean,
    showYearEvenIfCurrent: Boolean
): String {
    val timestampMillis = this * 1000L
    val cal = Calendar.getInstance(Locale.ENGLISH).apply {
        timeInMillis = timestampMillis
    }

    return if (DateUtils.isToday(timestampMillis)) {
        DateFormat.format(context.getTimeFormat(), cal).toString()
    } else {
        var format = "d/M/yyyy"

        if (!showYearEvenIfCurrent && isThisYear()) {
            format = "d/M"
        }

        if (!hideTimeAtOtherDays) {
            format += ", ${context.getTimeFormat()}"
        }

        DateFormat.format(format, cal).toString()
    }
}

fun Int.dp(context: Context): Int {
    return (this * context.resources.displayMetrics.density).toInt()
}

fun Int.convertSecondsToHMS(): Triple<Int, Int, Int> {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60
    return Triple(hours, minutes, seconds)
}






