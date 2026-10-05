package com.phamthang.base.rate

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.TextView
import android.widget.Toast
import com.vifarmer.ads.lib.Utils.EventTrackingHelper
import com.phamthang.base.extention.setEnableState
import com.phamthang.base.utils.Constants
import com.phamthang.base.R

@SuppressLint("StringFormatInvalid")
class RatingDialog(private val context: Context) : Dialog(context, R.style.CustomAlertDialog) {
    private var onPress: OnPress? = null
    private val tvTitle: TextView
    private val tvContent: TextView
    private val rtb: RatingBar
    private val imgIcon: ImageView
    private val imageView: ImageView
    private val editFeedback: EditText
    private val btnRate: Button
    private val btnLater: Button

    init {
        setContentView(R.layout.dialog_rating_app)
        val attributes = window!!.attributes
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT
        window!!.attributes = attributes
        window!!.setSoftInputMode(16)
        tvTitle = findViewById<View>(R.id.tvTitle) as TextView
        tvContent = findViewById<View>(R.id.tvContent) as TextView
        rtb = findViewById<View>(R.id.rtb) as RatingBar
        imgIcon = findViewById<View>(R.id.imgIcon) as ImageView
        imageView = findViewById<View>(R.id.imageView) as ImageView
        editFeedback = findViewById<View>(R.id.editFeedback) as EditText
        btnRate = findViewById<View>(R.id.btnRate) as Button
        btnLater = findViewById<View>(R.id.btnLater) as Button
        val appName = context.resources.getString(R.string.app_name)
        val text = context.resources.getString(R.string.If_you_enjoy_our, appName)
        tvContent.text = text
        rtb.progress = 5
        btnRate.setEnableState(rtb.progress != 0)
        setCancelable(false)
        onclick()
        changeRating()
        EventTrackingHelper.logEvent(context, Constants.TrackingKeys.rate_show)

    }

    interface OnPress {
        fun send(rate: Float)

        fun rating(rate: Float)

        fun cancel()

        fun later()
    }

    fun init(context: Context?, onPress: OnPress?) {
        this.onPress = onPress
    }

    fun changeRating() {
        rtb.onRatingBarChangeListener = OnRatingBarChangeListener { ratingBar, rating, fromUser ->
            val getRating = rtb.rating.toString()
            when (getRating) {
                "1.0" -> {
                    editFeedback.visibility = View.GONE
                    btnRate.text = context.resources.getString(R.string.Rate_Us)
                    imgIcon.setImageResource(R.drawable.rating_1)
                }

                "2.0" -> {
                    editFeedback.visibility = View.GONE
                    btnRate.text = context.resources.getString(R.string.Rate_Us)
                    imgIcon.setImageResource(R.drawable.rating_2)
                }

                "3.0" -> {
                    editFeedback.visibility = View.GONE
                    btnRate.text = context.resources.getString(R.string.Rate_Us)
                    imgIcon.setImageResource(R.drawable.rating_3)
                }

                "4.0" -> {
                    editFeedback.visibility = View.GONE
                    btnRate.text = context.resources.getString(R.string.Rate_Us)
                    imgIcon.setImageResource(R.drawable.rating_4)
                }

                "5.0" -> {
                    editFeedback.visibility = View.GONE
                    btnRate.text = context.resources.getString(R.string.Rate_Us)
                    imgIcon.setImageResource(R.drawable.rating_4)
                }

                else -> {
                    btnRate.text = context.resources.getString(R.string.Rate_Us)
                    editFeedback.visibility = View.GONE
                    imgIcon.setImageResource(R.drawable.rating_0)
                }
            }
        }
    }

    val newName: String
        get() = editFeedback.text.toString()

    val rating: String
        get() = rtb.rating.toString()

    private fun onclick() {
        btnRate.setOnClickListener(View.OnClickListener {
            if (rtb.rating == 0f) {
                Toast.makeText(context, context.resources.getString(R.string.Please_feedback), Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if (rtb.rating <= 3.0) {
                imageView.visibility = View.GONE
                imgIcon.visibility = View.GONE
                onPress!!.send(rtb.rating)
                Toast.makeText(context, context.resources.getString(R.string.thank_you_for_choosing_us), Toast.LENGTH_SHORT).show()
                EventTrackingHelper.logEvent(context, Constants.TrackingKeys.rate_submit)
            } else {
                imageView.visibility = View.GONE
                imgIcon.visibility = View.VISIBLE
                onPress!!.rating(rtb.rating)
                Toast.makeText(context, context.resources.getString(R.string.thank_you_for_choosing_us), Toast.LENGTH_SHORT).show()
                EventTrackingHelper.logEvent(context, Constants.TrackingKeys.rate_submit)
            }
            dismiss()
        })
        btnLater.setOnClickListener {
            onPress!!.later()
            EventTrackingHelper.logEvent(context, Constants.TrackingKeys.rate_not_now)
            dismiss()
        }
    }
}
