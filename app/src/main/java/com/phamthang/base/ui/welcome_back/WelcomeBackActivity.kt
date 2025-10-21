package com.phamthang.base.ui.welcome_back

import androidx.activity.enableEdgeToEdge
import com.amazic.library.ads.admob.AdmobApi
import com.amazic.library.ads.app_open_ads.AppOpenManager
import com.amazic.library.ads.callback.AppOpenCallback
import com.phamthang.base.databinding.ActivityWelcomeBackBinding
import com.google.android.gms.ads.appopen.AppOpenAd
import com.phamthang.base.base.BaseActivity
import com.phamthang.base.extention.tap
import com.phamthang.base.utils.Constants

class WelcomeBackActivity : BaseActivity<WelcomeBackViewModel, ActivityWelcomeBackBinding>() {
    override fun createViewModel(): Class<WelcomeBackViewModel> = WelcomeBackViewModel::class.java

    override fun getViewDataBinding(): ActivityWelcomeBackBinding = ActivityWelcomeBackBinding.inflate(layoutInflater)

    override fun initView() {
        enableEdgeToEdge()
        //Preload
        AppOpenManager.getInstance().loadAd(this, AdmobApi.getInstance().getListIDByName(Constants.RemoteKeys.resume_wb), object : AppOpenCallback() {
            override fun onAdLoaded(ad: AppOpenAd?) {
                super.onAdLoaded(ad)
                binding.tvStart.alpha = 1f
                binding.tvStart.isEnabled = true
            }

            override fun onAdFailedToLoad() {
                super.onAdFailedToLoad()
                binding.tvStart.alpha = 1f
                binding.tvStart.isEnabled = true
            }
        }, Constants.RemoteKeys.resume_wb)

        binding.tvStart.tap {
            //Show resume preload
            AppOpenManager.getInstance().showAdIfAvailableWelcomeBack(
                this,
                AdmobApi.getInstance().getListIDByName(Constants.RemoteKeys.resume_wb),
                object : AppOpenCallback() {
                    override fun onAdFailedToShowFullScreenContent() {
                        super.onAdFailedToShowFullScreenContent()
                        finish()
                    }

                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()
                        finish()
                    }
                }, Constants.RemoteKeys.resume_wb
            )
        }
            //Load and show resume
            /*AppOpenManager.getInstance().loadAndShowResumeAds(this, AdmobApi.getInstance().getListIDByName(Constants.RemoteKeys.resume_wb), object : AppOpenCallback() {
                override fun onAdFailedToLoad() {
                    super.onAdFailedToLoad()
                    finish()
                }

                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    finish()
                }

                override fun onAdFailedToShowFullScreenContent() {
                    super.onAdFailedToShowFullScreenContent()
                    finish()
                }
            }, Constants.RemoteKeys.resume_wb)*//*
        }

        */
    /*    loadNative(
            binding.frAds,
            Constants.RemoteKeys.native_wb,
            Constants.RemoteKeys.native_wb,
            Constants.AdsKey.native_wb,
            Constants.AdsKey.native_wb,
            R.layout.native_large_ads_with_button_above,
            R.layout.shimmer_native_large_with_button_above
        )*/
    }

    override fun bindViewModel() {

    }

    override fun onBackPressCustom() {

    }
}
