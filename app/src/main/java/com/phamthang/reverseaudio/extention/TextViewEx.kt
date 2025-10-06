package com.phamthang.reverseaudio.extention

import android.annotation.SuppressLint
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun TextView.setGradientText(
    colors: IntArray,
    positions: FloatArray? = null,
    orientation: Orientation = Orientation.HORIZONTAL
) {
    post {
        val width = this.width.toFloat()
        val height = this.height.toFloat()

        val (x0, y0, x1, y1) = when (orientation) {
            Orientation.HORIZONTAL -> listOf(0f, 0f, width, 0f)
            Orientation.VERTICAL -> listOf(0f, 0f, 0f, height)
            Orientation.DIAGONAL_TL_BR -> listOf(0f, 0f, width, height)  // chéo từ trên trái xuống dưới phải
            Orientation.DIAGONAL_BL_TR -> listOf(0f, height, width, 0f)  // chéo từ dưới trái lên trên phải
        }

        val shader = LinearGradient(
            x0, y0, x1, y1,
            colors,
            positions,
            Shader.TileMode.CLAMP
        )
        paint.shader = shader
        invalidate()
    }
}

enum class Orientation {
    HORIZONTAL,
    VERTICAL,
    DIAGONAL_TL_BR,
    DIAGONAL_BL_TR
}

@SuppressLint("SetTextI18n")
fun TextView.setCustomDate() {
    val calendar = Calendar.getInstance()
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val month = SimpleDateFormat("MMMM", Locale.ENGLISH).format(calendar.time)
    val year = calendar.get(Calendar.YEAR)

    val suffix = when {
        day in 11..13 -> "th"
        day % 10 == 1 -> "st"
        day % 10 == 2 -> "nd"
        day % 10 == 3 -> "rd"
        else -> "th"
    }

    this.text = "$month, $day$suffix $year"
}


fun TextView.setUnderLine(){
    this.paintFlags or Paint.UNDERLINE_TEXT_FLAG
}