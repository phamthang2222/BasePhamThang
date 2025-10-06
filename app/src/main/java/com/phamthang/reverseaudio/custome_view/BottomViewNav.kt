package com.phamthang.reverseaudio.custome_view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.phamthang.reverseaudio.R
import com.phamthang.reverseaudio.databinding.LayoutBottomViewNavBinding

@SuppressLint("ClickableViewAccessibility")
class BottomViewNav @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = LayoutBottomViewNavBinding.inflate(LayoutInflater.from(context), this, true)
    private var colorOn: Int = context.getColor(R.color.white)
    private var colorOff: Int = context.getColor(R.color.color_off)

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BottomViewNav)
        val backgroundIcon = typedArray.getDrawable(R.styleable.BottomViewNav_backgroundIcon)
        val icon = typedArray.getDrawable(R.styleable.BottomViewNav_iconBottomNav)
        val text = typedArray.getString(R.styleable.BottomViewNav_textTitle)

        colorOn = typedArray.getColor(R.styleable.BottomViewNav_textTitleColorOn,  context.getColor(R.color.blue_in_app))
        colorOff = typedArray.getColor(R.styleable.BottomViewNav_textTitleColorOff,  context.getColor(R.color.text_nav_unselected))

        typedArray.recycle()

        binding.icon.setImageDrawable(icon)
        binding.bgIcon.background = backgroundIcon
        binding.text.text = text
        binding.text.setTextColor(colorOff)

    }

    override fun setActivated(activated: Boolean) {
        super.setActivated(activated)
        binding.bgIcon.isActivated = activated
        binding.icon.isActivated = activated
        binding.text.isActivated = activated
        binding.text.setTextColor(if (activated) colorOn else colorOff)

    }
}