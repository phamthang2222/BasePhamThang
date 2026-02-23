package com.phamthang.base.rate

import android.app.Activity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.play.core.review.ReviewManagerFactory
import com.phamthang.base.extention.logDebug
import com.phamthang.base.share_pref.SharePrefApp

fun Activity.showRate(
    sharePrefApp: SharePrefApp,
    onSuccess: () -> Unit,
    onDismiss: () -> Unit,
){
    val ratingDialog = RatingDialog(this)
    ratingDialog.init(this, object : RatingDialog.OnPress {
        override fun send(rate: Float) {
            val manager = ReviewManagerFactory.create(this@showRate)
            manager.requestReviewFlow().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    val flow = manager.launchReviewFlow(this@showRate, reviewInfo)
                    flow.addOnSuccessListener(OnSuccessListener { result: Void? ->

                    })
                    sharePrefApp.isRated = true
                    logDebug(tagSpecial = "showRatingDialog", message = "send")
                    onSuccess()
                }
            }
        }

        override fun rating(rate: Float) {
            sharePrefApp.isRated = true
            onSuccess()
            logDebug(tagSpecial = "showRatingDialog", message = "rating")
        }

        override fun cancel() {
            onDismiss()
            logDebug(tagSpecial = "showRatingDialog", message = "cancel")
        }

        override fun later() {
            onDismiss()
            logDebug(tagSpecial = "showRatingDialog", message = "later")
        }
    })
    ratingDialog.show()
}