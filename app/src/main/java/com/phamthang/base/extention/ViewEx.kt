package com.phamthang.base.extention

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowInsetsController
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

fun View.onGlobalLayout(callback: () -> Unit) {
    viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (viewTreeObserver != null) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                callback()
            }
        }
    })
}

inline val View.layoutInflater: LayoutInflater get() = LayoutInflater.from(context)
fun Activity.changeStatusBarColor(color: Int, lightStatusBar: Boolean = false) {
    this.window?.statusBarColor = resources.getColor(color, theme)
    if (lightStatusBar)
        this.lightStatusBar()

}

fun Activity.lightStatusBar() {
    val decorView: View? = this.window?.decorView
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val wic = decorView?.windowInsetsController
        wic?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
    } else
        decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
}

fun Activity.setGradientText(
    textView: TextView,
    colors: IntArray,
    positions: FloatArray? = null,
    isVertical: Boolean = false
) {
    textView.post {
        val width = textView.width.toFloat()
        val height = textView.height.toFloat()

        val (x0, y0, x1, y1) = if (isVertical) {
            listOf(0f, 0f, 0f, height)
        } else {
            listOf(0f, 0f, width, 0f)
        }

        val shader = LinearGradient(
            x0, y0, x1, y1,
            colors,
            positions,
            Shader.TileMode.CLAMP
        )

        textView.paint.shader = shader
        textView.invalidate()
    }
}


fun View.tap(action: (view: View?) -> Unit) {
    setOnClickListener(object : TapListener() {
        override fun onTap(v: View?) {
            action(v)
        }
    })
}

fun View.tapWithVibrate(action: (view: View?) -> Unit) {
    setOnClickListener(object : TapListener() {
        override fun onTap(v: View?) {
            v?.context?.vibrate()
            action(v)
        }
    })
}

fun View.clickVibrate(action: (view: View?) -> Unit) {
    setOnClickListener {
        context.vibrate()
        action(it)
    }
}

@SuppressLint("ClickableViewAccessibility")
fun View.clickWithBounce(
    durationAnimation: Long = 50L,
    isVibrate: Boolean = false,
    scale: Float = 0.95f,
    onClickAction: () -> Unit
) {
    this.setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if(isVibrate) v.context.vibrate()
                v.animate()
                    .scaleX(scale)
                    .scaleY(scale)
                    .setDuration(100L)
                    .start()
                true
            }
            MotionEvent.ACTION_UP -> {
                v.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(durationAnimation)
                    .setInterpolator(OvershootInterpolator())
                    .withEndAction {
                        onClickAction.invoke()
                    }
                    .start()
                true
            }
            MotionEvent.ACTION_CANCEL -> {
                v.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(durationAnimation)
                    .setInterpolator(OvershootInterpolator())
                    .start()
                true
            }
            else -> false
        }
    }
}




@SuppressLint("ClickableViewAccessibility")
fun View.singleAndLongClickRepeat(
    onClick: (View) -> Unit,
    onLongPressRepeat: (View) -> Unit,
    interval: Long = 100
) {
    var isLongPress = false
    var handler: Handler? = null
    var runnable: Runnable? = null

    setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isLongPress = false
                handler = Handler(Looper.getMainLooper())
                runnable = Runnable {
                    isLongPress = true
                    onLongPressRepeat(v)
                    handler.postDelayed(runnable!!, interval)
                }
                handler.postDelayed(runnable, 500)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                handler?.removeCallbacks(runnable!!)
                if (!isLongPress) {
                    onClick(v)
                }
            }
        }
        true
    }
}


var lastClickTime = 0L
fun View.onSingleClick(action: () -> Unit) {
    this.setOnClickListener {
        if (System.currentTimeMillis() - lastClickTime >= 500) {
            action()
            lastClickTime = System.currentTimeMillis()
        }
    }
}
fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.visibleOrGone(show: Boolean) {
    visibility = if (show) View.VISIBLE else View.GONE
}

fun View.setEnableState(isEnabledState: Boolean) {
    alpha = if (isEnabledState) 1f else 0.3f
    isEnabled = isEnabledState
    isClickable = isEnabledState
    isFocusable = isEnabledState
}

//set all child enable state
fun View.setEnableStateRecursively(isEnabledState: Boolean, alphaValue : Float = 0.3f) {
    alpha = if (isEnabledState) 1f else alphaValue
    isEnabled = isEnabledState
    isClickable = isEnabledState
    isFocusable = isEnabledState

    if (this is ViewGroup) {
        for (i in 0 until childCount) {
            getChildAt(i).setEnableStateRecursively(isEnabledState)
        }
    }
}


fun View.hideKeyboard() {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showAnimated(fromTop: Boolean = false, duration: Long = 300) {
    if (visibility != View.VISIBLE) {
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val screenOffset = if (fromTop) -screenHeight else screenHeight

        translationY = screenOffset.toFloat()
        alpha = 0f
        visibility = View.VISIBLE

        animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }
}


fun View.hideAnimated(fromTop: Boolean = false, duration: Long = 300) {
    val screenHeight = Resources.getSystem().displayMetrics.heightPixels
    val screenOffset = if (fromTop) -screenHeight else screenHeight

    animate()
        .translationY(screenOffset.toFloat())
        .alpha(0f)
        .setDuration(duration)
        .setInterpolator(AccelerateInterpolator())
        .withEndAction {
            visibility = View.GONE
        }
        .start()
}



fun View.dismissWithFade(
    windowManager: WindowManager,
    duration: Long = 300L,
    onEnd: (() -> Unit)? = null
) {
    animate()
        .alpha(1f)
        .setDuration(duration)
        .withEndAction {
            try {
                windowManager.removeView(this)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                onEnd?.invoke()
            }
        }
        .start()
}

fun View.setFullscreenMode(isFullscreen: Boolean, portraitRatio: String? = "16:9") {
    if (this.layoutParams !is ConstraintLayout.LayoutParams) {
        return
    }

    val params = this.layoutParams as ConstraintLayout.LayoutParams

    if (isFullscreen) {
        params.width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
        params.height = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT

        params.dimensionRatio = null

        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
    } else {
        params.width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
        params.height = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT

        params.dimensionRatio = portraitRatio

        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID

        params.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
    }

    this.layoutParams = params
}

