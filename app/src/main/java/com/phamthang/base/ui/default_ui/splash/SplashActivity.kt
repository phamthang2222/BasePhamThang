package com.phamthang.base.ui.default_ui.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.amazic.library.Utils.EventTrackingHelper
import com.amazic.library.Utils.NetworkUtil
import com.amazic.library.Utils.RemoteConfigHelper
import com.amazic.library.ads.admob.Admob
import com.amazic.library.ads.admob.AdmobApi
import com.amazic.library.ads.app_open_ads.AppOpenManager
import com.amazic.library.ads.callback.AppOpenCallback
import com.amazic.library.ads.callback.InterCallback
import com.amazic.library.ads.callback.NativeCallback
import com.amazic.library.ads.splash_ads.AsyncSplash
import com.amazic.library.update_app.UpdateApplicationManager
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.InstallStatus
import com.phamthang.base.R
import com.phamthang.base.base.BaseActivity
import com.phamthang.base.databinding.ActivitySplashBinding
import com.phamthang.base.extention.toast
import com.phamthang.base.ui.default_ui.language.LanguageStartActivity
import com.phamthang.base.ui.default_ui.no_internet.NoInternetActivity
import com.phamthang.base.ui.default_ui.welcome_back.WelcomeBackActivity
import com.phamthang.base.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        var appUpdateManager: AppUpdateManager? = null
        var installStateUpdatedListener: InstallStateUpdatedListener? = null
        var nativeLanguageMainPreload: NativeAd? = null
        var nativeLanguageClickPreload: NativeAd? = null
        var isShowNativeLanguagePreloadAtSplash = false
        var isShowNativeLanguageClickPreloadAtSplash = false
    }

    private val openCallback = object : AppOpenCallback() {
        override fun onNextAction() {
            super.onNextAction()
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
            && intent.action != null && intent.action == Intent.ACTION_MAIN
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

        UpdateApplicationManager.getInstance().init(this, object : UpdateApplicationManager.IonUpdateApplication {
            override fun onUpdateApplicationFail() {
                toast(getString(R.string.update_application_fail))
                handleAsyncSplashJustOnce()
            }

            override fun onUpdateApplicationSuccess() {
            }

            override fun onMustNotUpdateApplication() {
                handleAsyncSplashJustOnce()
            }

            override fun requestUpdateFail() {
                handleAsyncSplashJustOnce()
            }
        })

        if (NetworkUtil.isNetworkActive(this)) {
            UpdateApplicationManager.getInstance().setUseFlexibleUpdate()
            appUpdateManager = UpdateApplicationManager.getInstance().checkVersionPlayStore(
                this,
                true,
                false,
                getString(R.string.new_update_available),
                getString(R.string.upgrade_now_for_a_smoother_experience_bug_fixes_for_better_performance),
                getString(R.string.update_now),
                getString(R.string.no)
            )
            installStateUpdatedListener = InstallStateUpdatedListener { state ->
                if (state.installStatus() == InstallStatus.DOWNLOADING ||
                    state.installStatus() == InstallStatus.FAILED ||
                    state.installStatus() == InstallStatus.CANCELED ||
                    state.installStatus() == InstallStatus.UNKNOWN
                ) {
                    handleAsyncSplashJustOnce()
                } else if (state.installStatus() == InstallStatus.DOWNLOADED) {
                    Toast.makeText(applicationContext, applicationContext.getString(R.string.updated_and_ready_welcome_back), Toast.LENGTH_SHORT)
                        .show()
                    appUpdateManager?.completeUpdate()
                }
            }
            installStateUpdatedListener?.let { appUpdateManager?.registerListener(it) }
        } else {
            val bundle = Bundle()
            bundle.putString(Constants.ScreenKey.SCREEN, Constants.ScreenKey.SPLASH_ACTIVITY)
            showActivity(NoInternetActivity::class.java, bundle)
            finish()
        }
    }

    private fun handleAsyncSplashJustOnce() {
        if (!isHandleAsyncSplash) {
            AsyncSplash.getInstance().init(
                activity = this@SplashActivity,
                appOpenCallback = openCallback,
                interCallback = interCallback,
                adjustKey = getString(R.string.adjust_key),
                linkServer = getString(R.string.link_server),
                appId = getString(R.string.app_id),
                jsonIdAdsDefault = jsonIdAdsDefault
            )
            AsyncSplash.getInstance().setDebug(false)
            AsyncSplash.getInstance().setAsyncSplashAds()
            AsyncSplash.getInstance().setListTurnOffRemoteKeys(addListTurnOffRemoteKeys())
            AsyncSplash.getInstance().setInitWelcomeBackAboveResumeAds(WelcomeBackActivity::class.java)
            AsyncSplash.getInstance().setUseAdPreloading(true)
            AsyncSplash.getInstance().handleAsync(
                this@SplashActivity, this@SplashActivity, lifecycleScope,
                onAsyncSplashDone = {
                    preloadANativeMainLanguage()
                    preloadANativeClickLanguage()
                    AsyncSplash.getInstance().setKeyNumberPreloading("number_ad_preload")
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
        val intent = Intent(this, LanguageStartActivity::class.java).apply {
            putExtras(bundle)
        }
        startActivity(intent)
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
                delay(30)
            }
            onComplete()
        }
    }
}
