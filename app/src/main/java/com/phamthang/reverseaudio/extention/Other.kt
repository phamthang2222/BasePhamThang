package com.phamthang.reverseaudio.extention

import kotlin.math.abs

fun List<Int>.indexOfClosest(target: Any): Int {
    val targetInt = when (target) {
        is Int -> target
        is Double -> target.toInt()
        is Float -> target.toInt()
        is String -> target.toIntOrNull()
        else -> null
    } ?: return 0

    return this.withIndex()
        .minByOrNull { abs(it.value - targetInt) }
        ?.index ?: 0
}

