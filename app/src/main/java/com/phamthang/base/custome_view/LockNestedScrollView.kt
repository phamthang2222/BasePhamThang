package com.phamthang.base.custome_view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView

class LockNestedScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr) {
    var isLocked = false

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return !isLocked && super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return !isLocked && super.onInterceptTouchEvent(ev)
    }
}