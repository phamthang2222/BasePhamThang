package com.phamthang.base.extention

import java.text.DecimalFormat

fun Double.toInch(): Double {
    return this / 2.54
}

fun Double.toLb(): Double {
    return this * 2.20462
}

fun Double.toKg(): Double {
    return this / 2.20462
}

// Tối đa 2 chữ số sau dấu thập phân, bỏ số 0 dư
fun Double.toCleanString(): String {
    val df = DecimalFormat("#.##")
    return df.format(this)
}

fun Double.toFeetInchString(): String {
    val totalInches = this / 2.54
    val feet = totalInches.toInt() / 12
    val inches = totalInches % 12
    return "$feet’ ${"%.1f".format(inches).removeSuffix(".0")}”"

}

fun String.toFeetInchPair(): Pair<Int, Double>? {
    val regex = Regex("""(\d+)[’']\s*(\d+(?:\.\d+)?)[”"]""")
    val match = regex.find(this)
    return match?.let {
        val feet = it.groupValues[1].toInt()
        val inches = it.groupValues[2].toDouble()
        feet to inches
    }
}


fun Pair<Int, Double>.toCm(): Double {
    val (feet, inches) = this
    val cm = feet * 30.48 + inches* 2.54
    return cm
}

fun Pair<Int, Double>.toInch(): Double {
    val (feet, inches) = this
    return feet * 12 + inches
}

fun Double.toLevelBMI(): Int {
    return when (this) {
        in 0.0..15.9 -> 1
        in 16.0..16.9 -> 2
        in 17.0..18.4 -> 3
        in 18.5..24.9 -> 4
        in 25.0..29.9 -> 5
        in 30.0..34.9 -> 6
        in 35.0..39.9 -> 7
        else -> 8
    }
}

fun Double.withSign(): String {
    return when {
        this > 0 -> "+${toCleanString()}"
        this < 0 -> toCleanString()
        else -> "0"
    }
}


