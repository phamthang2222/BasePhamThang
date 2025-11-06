package com.phamthang.base.extention

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PorterDuff
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.phamthang.base.R

fun ImageView.loadImage(imageUrl: String, progressBar: ProgressBar? = null) {
    Glide.with(this.context)
        .load(imageUrl)
        .placeholder(R.drawable.bg_btn_ads)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                progressBar?.gone()
                return false
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                progressBar?.gone()
                return false
            }
        })
        .into(this)
}

fun ImageView.loadImageWithViewLoading(imageUrl: String, loadingView: View? = null) {
    loadingView?.let {
        it.visibility = View.VISIBLE
        if (it is LottieAnimationView) {
            it.playAnimation()
            it.loop(true)
        }
    }

    Glide.with(this.context)
        .load(imageUrl)
        .placeholder(R.drawable.bg_btn_ads)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                loadingView?.let {
                    if (it is LottieAnimationView) {
                        it.cancelAnimation()
                    }
                    it.visibility = View.GONE
                }
                return false
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                loadingView?.let {
                    if (it is LottieAnimationView) {
                        it.cancelAnimation()
                    }
                    it.visibility = View.GONE
                }
                return false
            }
        })
        .into(this)
}

fun ImageView.loadImageFromUrl(
    imageUrl: String,
    loadingView: View? = null
) {
    loadingView?.let {
        it.visibility = View.VISIBLE
        if (it is LottieAnimationView) {
            it.playAnimation()
            it.loop(true)
        }
    }

    Glide.with(this.context)
        .load(imageUrl)
        .placeholder(R.drawable.bg_thumbnail_df)
        .downsample(DownsampleStrategy.AT_LEAST)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                loadingView?.let {
                    if (it is LottieAnimationView) {
                        it.cancelAnimation()
                    }
                    it.visibility = View.GONE
                }
                return false
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                loadingView?.let {
                    if (it is LottieAnimationView) {
                        it.cancelAnimation()
                    }
                    it.visibility = View.GONE
                }
                return false
            }
        })
        .into(this)
}


fun ImageView.loadImage(imgRes: Int, progressBar: ProgressBar? = null) {
    Glide.with(this.context)
        .load(imgRes)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                progressBar?.gone()
                return false
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                progressBar?.gone()
                return false
            }
        })
        .into(this)
}

fun ImageView.loadImage(imgUri: Uri, progressBar: ProgressBar? = null) {
    Glide.with(this.context)
        .load(imgUri)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                progressBar?.gone()
                return false
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                progressBar?.gone()
                return false
            }
        })
        .into(this)
}

fun ImageView.loadImage(bitmap: Bitmap, progressBar: ProgressBar? = null) {
    Glide.with(this.context)
        .load(bitmap)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                progressBar?.gone()
                return false
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                progressBar?.gone()
                return false
            }
        })
        .into(this)
}


//filter
fun ImageView.setEnableState(isEnabled: Boolean, disableColor: Int) {
    if (isEnabled) {
        clearColorFilter()
        isClickable = true
    } else {
        setColorFilter(disableColor, PorterDuff.Mode.SRC_IN)
        isClickable = false
    }
}

//set resDraw
fun ImageView.setEnableState(state: Boolean, resDrawDisable: Int, resDrawEnable: Int) {
    if (state) {
        this.setImageResource(resDrawEnable)
        isClickable = true
        isEnabled = true
    } else {
        this.setImageResource(resDrawDisable)
        isClickable = false
        isEnabled = false
    }
}

//alpha
fun ImageView.setEnableState(state: Boolean, alphaValue: Float = 0.3f) {
    if (state) {
        this.alpha = 1f
        isClickable = true
        isEnabled = true
    } else {
        this.alpha = alphaValue
        isClickable = false
        isEnabled = false
    }
}


fun ImageView.getImageBounds(): RectF? {
    val drawable = drawable ?: return null
    val matrix = imageMatrix
    val values = FloatArray(9)
    matrix.getValues(values)

    val scaleX = values[Matrix.MSCALE_X]
    val scaleY = values[Matrix.MSCALE_Y]
    val transX = values[Matrix.MTRANS_X]
    val transY = values[Matrix.MTRANS_Y]

    val drawableWidth = drawable.intrinsicWidth
    val drawableHeight = drawable.intrinsicHeight

    val width = drawableWidth * scaleX
    val height = drawableHeight * scaleY

    return RectF(transX, transY, transX + width, transY + height)
}
