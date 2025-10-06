package com.phamthang.reverseaudio.custome_view

import android.app.Dialog
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.phamthang.reverseaudio.R

class LoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val lottieAnimationView: LottieAnimationView

    init {
        // Thiết lập layout cho custom view
        orientation = VERTICAL
        gravity = Gravity.CENTER
        setPadding(20, 20, 20, 20)

        // Tạo LottieAnimationView
        lottieAnimationView = LottieAnimationView(context).apply {
            layoutParams = LayoutParams(250.dpToPx(), 250.dpToPx()) // Thiết lập kích thước
            setAnimation(R.raw.loading)
            repeatMode = LottieDrawable.RESTART
            repeatCount = LottieDrawable.INFINITE
            scaleType = ImageView.ScaleType.CENTER_CROP
            playAnimation()
        }

        // Thêm LottieAnimationView vào LinearLayout
        addView(lottieAnimationView)
    }

    // Extension function để chuyển dp sang px
    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    companion object {
        fun createDialogLoading(context: Context): Dialog {
            return Dialog(context).apply {
                setContentView(LoadingView(context))
                setCancelable(false)
                window?.setBackgroundDrawableResource(R.color.transparent)
            }
        }
    }
}
