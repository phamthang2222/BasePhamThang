package com.phamthang.reverseaudio

import com.amazic.library.ads.app_open_ads.AppOpenManager
import com.amazic.library.application.AdsApplication

import kotlin.jvm.java

class App : AdsApplication(){

    override fun onCreate() {
        super.onCreate()
        SharedPrefManager.init(this)
        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity::class.java)

    }
    override fun getAppTokenAdjust(): String {
        return getString(R.string.adjust_key)
    }

    override fun getFacebookID(): String {
        return getString(R.string.facebook_app_id)
    }
}