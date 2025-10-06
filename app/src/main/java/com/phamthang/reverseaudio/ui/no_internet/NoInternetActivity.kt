package com.phamthang.reverseaudio.ui.no_internet

import android.content.Intent
import android.os.Build
import android.provider.Settings

import com.amazic.library.ads.app_open_ads.AppOpenManager
import com.phamthang.reverseaudio.databinding.ActivityNoInternetBinding
import com.phamthang.reverseaudio.base.BaseActivity
import com.phamthang.reverseaudio.extention.tap
import kotlin.jvm.java
import kotlin.jvm.javaClass

class NoInternetActivity : BaseActivity<NoInternetViewModel, ActivityNoInternetBinding>() {
    override fun createViewModel(): Class<NoInternetViewModel> = NoInternetViewModel::class.java
    override fun getViewDataBinding(): ActivityNoInternetBinding = ActivityNoInternetBinding.inflate(layoutInflater)

    override fun initView() {
        
    }

    override fun bindViewModel() {

        binding.tvTryAgain.tap {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // For Android 10 and above, prompt the user to enable Wi-Fi
                val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
                startActivity(panelIntent)
            } else {
                AppOpenManager.getInstance().disableAppResumeWithActivity(javaClass)
                // For Android 9 and below, enable Wi-Fi programmatically
                val wifiSettingsIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(wifiSettingsIntent)
            }
        }
    }

    override fun onBackPressCustom() {
        finishAffinity()
    }
}