package com.phamthang.base.ads

import android.app.Activity
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.vifarmer.ads.lib.Utils.RemoteConfigHelper
import com.vifarmer.ads.lib.admob.Admob
import com.vifarmer.ads.lib.admob.AdmobApi
import com.vifarmer.ads.lib.ads.app_open_ads.AppOpenManager
import com.vifarmer.ads.lib.callback.InterCallback
import com.vifarmer.ads.lib.callback.NativeCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.phamthang.base.R
import com.phamthang.base.ui.default_ui.splash.SplashActivity
import com.phamthang.base.utils.Constants
import com.vifarmer.ads.lib.ads.banner_ads.BannerBuilder
import com.vifarmer.ads.lib.ads.banner_ads.BannerManager
import com.vifarmer.ads.lib.ads.collapse_banner_ads.CollapseBannerBuilder
import com.vifarmer.ads.lib.ads.collapse_banner_ads.CollapseBannerManager
import com.vifarmer.ads.lib.ads.inter_ads.InterManager
import com.vifarmer.ads.lib.ads.native_ads.NativeBuilder
import com.vifarmer.ads.lib.ads.native_ads.NativeManager
import com.vifarmer.ads.lib.ads.reward_ads.RewardManager
import com.vifarmer.ads.lib.callback.RewardedCallback
import com.vifarmer.ads.lib.organic.TechManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

public object AdsHelper {
    fun dismissAppOpen(activity: Activity) {
        AppOpenManager.getInstance().disableAppResumeWithActivity(activity.javaClass)
    }

    fun enableAppOpen(activity: Activity, lifecycleOwner: LifecycleOwner) {
        if (activity !is SplashActivity) {
            lifecycleOwner.lifecycleScope.launch {
                delay(500L.milliseconds)
                AppOpenManager.getInstance().enableAppResumeWithActivity(activity.javaClass)
            }
        } else dismissAppOpen(activity)
    }

    //collapse banner
    fun loadCollapseBanner(
        activity: AppCompatActivity,
        lifecycleOwner: LifecycleOwner,
        adsKey: String,
        remoteKey: String,
        frAds: FrameLayout
    ): CollapseBannerManager? {
        var collapseBanner: CollapseBannerManager? = null
        if (Admob.getInstance().checkCondition(activity, remoteKey)) {
            val collapseBannerBuilder = CollapseBannerBuilder()
            collapseBannerBuilder.setListId(AdmobApi.getInstance().getListIDByName(adsKey))
            collapseBanner = CollapseBannerManager(activity, frAds, lifecycleOwner, collapseBannerBuilder, remoteKey)
            collapseBanner.setIntervalReloadBanner(
                RemoteConfigHelper.getInstance().get_config_long(activity, Constants.RemoteKeys.collap_reload_interval) * 1000
            )
            collapseBanner.setAlwaysReloadOnResume(true)
        }
        return collapseBanner
    }

    //banner
    fun loadBannerAds(
        activity: Activity,
        lifecycleOwner: LifecycleOwner,
        frAds: FrameLayout,
        adsKey: String
    ) {
        val bannerBuilder = BannerBuilder(activity, frAds, true)
        bannerBuilder.setListIdAdMain(AdmobApi.getInstance().getListIDByName(adsKey))
        val bannerManager = BannerManager(activity, lifecycleOwner, bannerBuilder, adsKey)
        bannerManager.setAlwaysReloadOnResume(true)
    }

    //inter


    //native
    fun loadNative(
        activity: Activity,
        lifecycleOwner: LifecycleOwner,
        frAds: FrameLayout,
        remoteKeyMain: String,
        remoteKeySecondary: String = remoteKeyMain,
        adsKeyMain: String = remoteKeyMain,
        adsKeySecondary: String = adsKeyMain,
        callBack: NativeCallback? = null,
        type: NativeType?
    ): NativeManager {
        val idLayoutShimmer: Int
        val idLayoutNative: Int
        val idLayoutNativeMeta: Int
        when {
            type == NativeType.NATIVE_LARGE_ABOVE && !TechManager.getInstance().isTech(activity) -> {
                idLayoutShimmer = R.layout.shimmer_native_large_with_button_above
                idLayoutNative = R.layout.native_large_ads_with_button_above
                idLayoutNativeMeta = R.layout.native_large_ads_with_button_above
            }

            type == NativeType.NATIVE_LARGE_BELOW ||
                    (TechManager.getInstance().isTech(activity) && type == NativeType.NATIVE_LARGE_ABOVE) -> {
                idLayoutShimmer = R.layout.shimmer_native_large_with_button_bellow
                idLayoutNative = R.layout.native_large_ads_with_button_bellow
                idLayoutNativeMeta = R.layout.native_large_ads_with_button_bellow
            }

            type == NativeType.NATIVE_SMALL_ABOVE && !TechManager.getInstance().isTech(activity) -> {
                idLayoutShimmer = R.layout.shimmer_native_small_with_button_above
                idLayoutNative = R.layout.native_small_ads_with_button_above
                idLayoutNativeMeta = R.layout.native_small_ads_with_button_above
            }

            type == NativeType.NATIVE_SMALL_BELOW ||
                    (TechManager.getInstance().isTech(activity) && type == NativeType.NATIVE_SMALL_ABOVE) -> {
                idLayoutShimmer = R.layout.shimmer_native_small_with_button_bellow
                idLayoutNative = R.layout.native_small_ads_with_button_bellow
                idLayoutNativeMeta = R.layout.native_small_ads_with_button_bellow
            }

            else -> {
                idLayoutShimmer = R.layout.shimmer_native_super_small
                idLayoutNative = R.layout.native_small_ads_meta
                idLayoutNativeMeta = R.layout.native_small_ads_meta
            }
        }
        val nativeBuilder = NativeBuilder(activity, frAds, idLayoutShimmer, idLayoutNative, idLayoutNativeMeta, true)
        nativeBuilder.setListIdAdMain(AdmobApi.getInstance().getListIDByName(adsKeyMain))
        //set secondary list id ads if load double native
        nativeBuilder.setListIdAdSecondary(AdmobApi.getInstance().getListIDByName(adsKeySecondary))
        if (callBack != null) {
            nativeBuilder.callback = callBack
        }
        val nativeManager = NativeManager(activity, lifecycleOwner, nativeBuilder, remoteKeyMain, remoteKeySecondary)
        nativeManager.setIntervalReloadNative(
            RemoteConfigHelper.getInstance().get_config_long(activity, Constants.RemoteKeys.interval_reload_native) * 1000
        )
        nativeManager.setAlwaysReloadOnResume(true)
        return nativeManager

    }

    fun loadNativeAll(
        activity: AppCompatActivity,
        frAds: FrameLayout,
        remoteKey: String = Constants.RemoteKeys.native_all,
        adsKey: String = Constants.RemoteKeys.native_all,
        lifecycleOwner: LifecycleOwner,
        callBack: NativeCallback? = null
    ) {
        loadNative(
            activity,
            lifecycleOwner,
            frAds,
            remoteKeyMain = remoteKey,
            adsKeyMain = adsKey,
            callBack = callBack,
            type = NativeType.NATIVE_LARGE_ABOVE
        )
    }

    fun loadAndShowInter(
        activity: Activity,
        remoteKey: String,
        adsKey: String,
        onNextAction: () -> Unit
    ) {
        InterManager.loadAndShowInterAds(
            activity,
            adsKey,
            remoteKey,
            object : InterCallback() {
                override fun onNextAction() {
                    super.onNextAction()
                    onNextAction.invoke()
                }

                override fun onAdFailedToLoad() {
                    super.onAdFailedToLoad()
                    if(remoteKey == Constants.RemoteKeys.inter_all){
                        val time = RemoteConfigHelper.getInstance().get_config_long(activity, "interval_inter_all") * 1000
                        if (time > 0) Admob.getInstance().setTimeInterval(time, false)
                    }
                }

                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    if(remoteKey == Constants.RemoteKeys.inter_all){
                        val time = RemoteConfigHelper.getInstance().get_config_long(activity, "interval_inter_all") * 1000
                        if (time > 0) Admob.getInstance().setTimeInterval(time, false)
                    }
                }

            }
        )
    }

    //reward
    fun loadReward(
        activity: Activity,
        adsKey: String,
        remoteKey: String = adsKey,
    ) {
        RewardManager.loadRewardAds(activity, adsKey, remoteKey)
    }

    fun showReward(
        activity: Activity,
        adsKey: String,
        remoteKey: String,
        isReloadAfterShow: Boolean,
        onNextAction: () -> Unit
    ) {
        var earnedReward = false
        RewardManager.showRewardAds(activity, adsKey, remoteKey, object : RewardedCallback() {
            override fun onUserEarnedReward() {
                super.onUserEarnedReward()
                earnedReward = true
            }

            override fun onNextAction() {
                super.onNextAction()
                if (earnedReward) {
                    onNextAction.invoke()
                }
            }
        }, isReloadAfterShow)
    }

    fun showDoubleReward(
        activity: Activity,
        adsKey: String,
        remoteKey: String,
        isReloadAfterShow: Boolean = true,
        onNextAction: () -> Unit
    ) {
        var earnedReward = false
        RewardManager.showRewardAds(
            activity, adsKey, remoteKey, object : RewardedCallback() {
                override fun onUserEarnedReward() {
                    super.onUserEarnedReward()
                    earnedReward = true
                }

                override fun onNextAction() {
                    super.onNextAction()
                    if (earnedReward) {
                        showReward(activity, adsKey, remoteKey, isReloadAfterShow) {
                            onNextAction.invoke()
                        }
                    }
                }
            },
            isReloadAfterShow
        )
    }
}