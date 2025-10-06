package com.phamthang.reverseaudio.custome_view
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class GradientStrokeTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    var strokeWidth = 0f
    var strokeColor = Color.WHITE
    var gradientColors: IntArray? = null
    var isVerticalGradient = false

    var customShadowColor: Int = Color.BLACK
    var customShadowRadius: Float = 8f
    var customShadowDx: Float = 4f
    var customShadowDy: Float = 4f

    private val strokePaint = Paint()
    private val fillPaint = Paint()

    init {
        strokePaint.isAntiAlias = true
        strokePaint.style = Paint.Style.STROKE
        strokePaint.textAlign = Paint.Align.LEFT

        fillPaint.isAntiAlias = true
        fillPaint.style = Paint.Style.FILL
        fillPaint.textAlign = Paint.Align.LEFT
    }

    override fun onDraw(canvas: Canvas) {
        val text = text.toString()
        val x = compoundPaddingLeft.toFloat()
        val y = baseline.toFloat()

        // Set up stroke paint
        strokePaint.color = strokeColor
        strokePaint.strokeWidth = strokeWidth
        strokePaint.textSize = textSize
        strokePaint.typeface = typeface
        strokePaint.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor)

        // Draw stroke
        canvas.drawText(text, x, y, strokePaint)

        // Set up fill paint
        fillPaint.textSize = textSize
        fillPaint.typeface = typeface
        fillPaint.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor)

        gradientColors?.let { colors ->
            val shader = if (isVerticalGradient) {
                LinearGradient(0f, 0f, 0f, height.toFloat(), colors, null, Shader.TileMode.CLAMP)
            } else {
                LinearGradient(0f, 0f, width.toFloat(), 0f, colors, null, Shader.TileMode.CLAMP)
            }
            fillPaint.shader = shader
        } ?: run {
            fillPaint.color = currentTextColor
        }

        // Draw fill
        canvas.drawText(text, x, y, fillPaint)
    }

    fun setGradientColors(colors: IntArray, isVertical: Boolean = false) {
        gradientColors = colors
        isVerticalGradient = isVertical
        invalidate()
    }

    fun setStroke(color: Int, width: Float) {
        strokeColor = color
        strokeWidth = width
        invalidate()
    }

    fun setShadow(color: Int, radius: Float, dx: Float, dy: Float) {
        customShadowColor = color
        customShadowRadius = radius
        customShadowDx = dx
        customShadowDy = dy
        invalidate()
    }
}
