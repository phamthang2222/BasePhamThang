package com.phamthang.base

import com.vifarmer.ads.lib.ads.app_open_ads.AppOpenManager
import com.phamthang.base.ui.default_ui.splash.SplashActivity
import com.vifarmer.ads.lib.admob.Admob
import com.vifarmer.ads.lib.application.AdsApplication
import dagger.hilt.android.HiltAndroidApp
import kotlin.jvm.java

@HiltAndroidApp
class App : AdsApplication() {

    override fun onCreate() {
        super.onCreate()
        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity::class.java)
        Admob.getInstance().tokenEventAdjust = ""
    }

    override fun getAppTokenAdjust(): String {
        return getString(R.string.adjust_key)
    }

    override fun getFacebookID(): String {
        return getString(R.string.facebook_app_id)
    }

    override fun buildDebug(): Boolean {
        return false
    }
}