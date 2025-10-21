package com.phamthang.base.utils

import android.app.Activity
import android.content.Intent
import androidx.core.app.ShareCompat
import androidx.core.net.toUri
import com.amazic.library.ads.app_open_ads.AppOpenManager
import com.phamthang.base.ui.home.HomeActivity

fun Activity.shareApp() {
    AppOpenManager.getInstance().disableAppResumeWithActivity(HomeActivity::class.java)
    ShareCompat.IntentBuilder.from(this)
        .setType("text/plain")
        .setChooserTitle("Chooser title")
        .setText("http://play.google.com/store/apps/details?id=" + this.packageName)
        .startChooser()
}

fun Activity.policy() {
    AppOpenManager.getInstance().disableAppResumeWithActivity(HomeActivity::class.java)
    val url = "https://amazic.net/Privacy-Policy-ASA314VoiceLock.html"
    val i = Intent(Intent.ACTION_VIEW)
    i.data = url.toUri()
    startActivity(i)
}