package com.phamthang.base.extention

import android.content.Context
import android.util.TypedValue
import java.text.DecimalFormat

fun Float.toCleanString(): String {
    val df = DecimalFormat("#.##")
    return df.format(this)
}

fun Float.toDp(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        context.resources.displayMetrics
    ).toInt()
}

