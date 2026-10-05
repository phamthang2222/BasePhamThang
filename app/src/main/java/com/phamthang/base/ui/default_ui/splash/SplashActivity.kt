package com.phamthang.base.ui.default_ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.vifarmer.ads.lib.Utils.NetworkUtil
import com.vifarmer.ads.lib.Utils.RemoteConfigHelper
import com.vifarmer.ads.lib.admob.Admob
import com.vifarmer.ads.lib.admob.AdmobApi
import com.vifarmer.ads.lib.ads.app_open_ads.AppOpenManager
import com.vifarmer.ads.lib.callback.AppOpenCallback
import com.vifarmer.ads.lib.callback.InterCallback
import com.vifarmer.ads.lib.callback.NativeCallback
import com.vifarmer.ads.lib.ads.splash_ads.AsyncSplash
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.phamthang.base.base.BaseActivity
import com.phamthang.base.utils.Constants
import com.phamthang.base.R
import com.phamthang.base.databinding.ActivitySplashBinding
import com.phamthang.base.ui.default_ui.language.LanguageStartActivity
import com.phamthang.base.ui.default_ui.no_internet.NoInternetActivity
import com.phamthang.base.ui.default_ui.welcome_back.WelcomeBackActivity
import com.vifarmer.ads.lib.Utils.EventTrackingHelper
import com.vifarmer.ads.lib.callback.BannerCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<SplashVM, ActivitySplashBinding>() {
    private val TAG = "SplashActivity"
    private var jsonIdAdsDefault = ""
    private var isHandleAsyncSplash = false
    private var countOpenSplash = 1L

    private var progressJob: Job? = null
    private var wasJobRunning = false
    private var timeStart = 0

    companion object {
        var isShowSplashAds = false
        var isCloseSplashAds = false
        var nativeLanguageMainPreload: NativeAd? = null
        var nativeLanguageClickPreload: NativeAd? = null
        var isShowNativeLanguagePreloadAtSplash = false
        var isShowNativeLanguageClickPreloadAtSplash = false
    }

    private val openCallback = object : AppOpenCallback() {
        override fun onNextAction() {
            super.onNextAction()
//            startNextAct()
        }

        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()
            isShowSplashAds = true
        }

        override fun onAdImpression() {
            super.onAdImpression()
            AppOpenManager.getInstance().appOpenAdSplash = null
        }

        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
            isCloseSplashAds = true
        }

        override fun onAdFailedToShowFullScreenContent() {
            super.onAdFailedToShowFullScreenContent()
            isCloseSplashAds = true
        }
    }

    private val interCallback = object : InterCallback() {
        override fun onNextAction() {
            super.onNextAction()
//            startNextAct()
        }

        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()
            isShowSplashAds = true
        }

        override fun onAdImpression() {
            super.onAdImpression()
            Admob.getInstance().interstitialAdSplash = null
        }

        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
            isCloseSplashAds = true
        }

        override fun onAdFailedToShowFullScreenContent() {
            super.onAdFailedToShowFullScreenContent()
            isCloseSplashAds = true
        }
    }

    override fun createViewModel(): Class<SplashVM> = SplashVM::class.java
    override fun getViewDataBinding(): ActivitySplashBinding = ActivitySplashBinding.inflate(layoutInflater)

    override fun initView() {
        if (!isTaskRoot
            && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
            && intent.action != null && intent.action.equals(Intent.ACTION_MAIN)
        ) {
            finish()
            return
        }
        enableEdgeToEdge()
        AsyncSplash.getInstance().setTimeSplashCheck()
        isShowSplashAds = false
        isCloseSplashAds = false

        EventTrackingHelper.logEvent(this, Constants.TrackingKeys.splash_open)
        countOpenSplash = RemoteConfigHelper.getInstance().get_config_long(this, "countOpenSplash") + 1
        if (countOpenSplash <= 10) {
            Log.d(TAG, "logEvent: $countOpenSplash")
            EventTrackingHelper.logEvent(this, Constants.TrackingKeys.splash_open + "_${countOpenSplash}")
        }
        RemoteConfigHelper.getInstance().set_config_long_commit(this, "countOpenSplash", countOpenSplash)
        RemoteConfigHelper.getInstance().set_config_commit(this, "isLogEventLanguageFO", false)



        if (NetworkUtil.isNetworkActive(this)) {
            handleAsyncSplashJustOnce()
        } else {
            val bundle = Bundle()
            bundle.putString(Constants.ScreenKey.SCREEN, Constants.ScreenKey.SPLASH_ACTIVITY)
            showActivity(NoInternetActivity::class.java, bundle)
            finish()
        }
    }

    private fun handleAsyncSplashJustOnce() {
        if (!isHandleAsyncSplash) { //important
            AsyncSplash.getInstance().init(
                activity = this@SplashActivity,
                appOpenCallback = openCallback,
                interCallback = interCallback,
                adjustKey = getString(R.string.adjust_key),
                linkServer = getString(R.string.link_server),
                appId = getString(R.string.app_id),
                jsonIdAdsDefault = jsonIdAdsDefault
            )
            //Test TechManager
            AsyncSplash.getInstance().setDebug(true) //Production set to false
            AsyncSplash.getInstance().setAsyncSplashAds()
            AsyncSplash.getInstance().setListTurnOffRemoteKeys(addListTurnOffRemoteKeys())
            AsyncSplash.getInstance().setInitWelcomeBackAboveResumeAds(WelcomeBackActivity::class.java)
            AsyncSplash.getInstance().handleAsync(
                this@SplashActivity, this@SplashActivity, lifecycleScope,
                onAsyncSplashDone = {
                    loadBannerSettingForTech{
                        preloadANativeMainLanguage()
                    }
                },
                onNoInternetAction = {
                    interCallback.onNextAction()
                }
            )
            isHandleAsyncSplash = true
        }
    }


    private fun addListTurnOffRemoteKeys(): MutableList<String> {
        return mutableListOf(
            Constants.RemoteKeys.native_language,
            Constants.RemoteKeys.native_language_2,
            Constants.RemoteKeys.native_intro,
            Constants.RemoteKeys.native_intro_2,
            Constants.RemoteKeys.native_intro_full,
            Constants.RemoteKeys.native_intro_full_2,
            Constants.RemoteKeys.native_intro_full1,
            Constants.RemoteKeys.native_intro_full1_2,
            Constants.RemoteKeys.inter_intro,
            Constants.RemoteKeys.native_per,
            Constants.RemoteKeys.resume_wb,
            Constants.RemoteKeys.native_wb,
            Constants.RemoteKeys.native_popup,
            Constants.RemoteKeys.collapse_banner,
            Constants.RemoteKeys.collapse_preview,
            Constants.RemoteKeys.native_all,
            Constants.RemoteKeys.inter_all,
            Constants.RemoteKeys.native_pin,
            Constants.RemoteKeys.rewarded_items,
            Constants.RemoteKeys.rewarded_all,
        )
    }

    override fun bindViewModel() {

    }

    override fun onBackPressCustom() {

    }

    private fun preloadANativeMainLanguage() {
        Admob.getInstance().loadNativeAds(
            this,
            AdmobApi.getInstance().getListIDByName(Constants.AdsKey.native_language),
            object : NativeCallback() {
                override fun onNativeAdLoaded(nativeAd: NativeAd?) {
                    super.onNativeAdLoaded(nativeAd)
                    nativeLanguageMainPreload = nativeAd
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    isShowNativeLanguagePreloadAtSplash = true
                }
            }, Constants.RemoteKeys.native_language
        )
    }

    private fun loadBannerSettingForTech(onDone: () -> Unit) {
        var isDoneCalled = false
        val safeDone = {
            if (!isDoneCalled) {
                isDoneCalled = true
                onDone()
            }
        }
        Admob.getInstance().loadBannerAds(
            this,
            AdmobApi.getInstance().getListIDByName(Constants.AdsKey.banner_setting),
            binding.frAds,
            object : BannerCallback() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    safeDone()
                }

                override fun onAdFailedToLoad() {
                    super.onAdFailedToLoad()
                    safeDone()
                }
            },
            {
                safeDone()
            },
            Constants.RemoteKeys.banner_setting
        )
    }
    private fun preloadANativeClickLanguage() {
        Admob.getInstance().loadNativeAds(
            this,
            AdmobApi.getInstance().getListIDByName(Constants.AdsKey.native_click),
            object : NativeCallback() {
                override fun onNativeAdLoaded(nativeAd: NativeAd?) {
                    super.onNativeAdLoaded(nativeAd)
                    nativeLanguageClickPreload = nativeAd
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    isShowNativeLanguageClickPreloadAtSplash = true
                }
            }, Constants.RemoteKeys.native_click
        )
    }

    private fun startNextAct() {
        progressJob?.cancel()

        val bundle = Bundle()
        bundle.putString(Constants.ScreenKey.SCREEN, Constants.ScreenKey.SPLASH_ACTIVITY)
        showActivity(LanguageStartActivity::class.java, bundle)
        finish()

    }

    override fun onResume() {
        super.onResume()
        AsyncSplash.getInstance().checkShowSplashWhenFail()
        if (wasJobRunning || progressJob == null || !progressJob!!.isActive) {
            startProgress(timeStart) {
                startNextAct()
            }
        }
    }

    private fun startProgress(timeStart: Int, onComplete: () -> Unit) {
        progressJob?.cancel()

        progressJob = lifecycleScope.launch {
            for (i in timeStart..99) {
                binding.progressIndicator.progress = i
                binding.tvPercent.text = getString(R.string.loading, i)
                this@SplashActivity.timeStart = i
                delay(30.milliseconds)
            }
            onComplete()
        }
    }
}