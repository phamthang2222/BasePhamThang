package com.phamthang.base.ads

import android.app.Activity
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.amazic.library.Utils.RemoteConfigHelper
import com.amazic.library.ads.admob.Admob
import com.amazic.library.ads.admob.AdmobApi
import com.amazic.library.ads.app_open_ads.AppOpenManager
import com.amazic.library.ads.banner_ads.BannerBuilder
import com.amazic.library.ads.banner_ads.BannerManager
import com.amazic.library.ads.callback.InterCallback
import com.amazic.library.ads.callback.NativeCallback
import com.amazic.library.ads.callback.RewardedCallback
import com.amazic.library.ads.collapse_banner_ads.CollapseBannerBuilder
import com.amazic.library.ads.collapse_banner_ads.CollapseBannerManager
import com.amazic.library.ads.inter_ads.InterManager
import com.amazic.library.ads.native_ads.NativeBuilder
import com.amazic.library.ads.native_ads.NativeManager
import com.amazic.library.ads.reward_ads.RewardManager
import com.amazic.library.organic.TechManager
import com.phamthang.base.R
import com.phamthang.base.ui.default_ui.splash.SplashActivity
import com.phamthang.base.utils.Constants

object AdsHelper {
    fun dismissAppOpen(activity: Activity) {
        AppOpenManager.getInstance().disableAppResumeWithActivity(activity.javaClass)
    }

    fun enableAppOpen(activity: Activity) {
        if (activity !is SplashActivity) {
            AppOpenManager.getInstance().enableAppResumeWithActivity(activity.javaClass)
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
    fun loadAndShowInter(
        activity: Activity,
        remoteKey: String,
        adsKey: String = remoteKey,
        onNextAction: () -> Unit
    ) {
        InterManager.loadAndShowInterAds(activity, adsKey, remoteKey, object : InterCallback() {
            override fun onNextAction() {
                super.onNextAction()
                onNextAction.invoke()
            }
        })
    }

    fun loadAndShowInterAll(
        activity: Activity,
        adsKey: String = Constants.AdsKey.inter_all,
        remoteKey: String = Constants.RemoteKeys.inter_all,
        isOpenActivityAfterShowInterAds: Boolean = true,
        onNextAction: () -> Unit
    ) {
        if (!isOpenActivityAfterShowInterAds) {
            Admob.getInstance().isOpenActivityAfterShowInterAds = false
        }
        InterManager.loadAndShowInterAds(activity, adsKey, remoteKey, object : InterCallback() {
            override fun onNextAction() {
                super.onNextAction()
                onNextAction.invoke()
            }

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                val time = RemoteConfigHelper.getInstance().get_config_long(activity, Constants.RemoteKeys.interval_inter_all) * 1000
                if (time > 0) Admob.getInstance().setTimeInterval(time, false)
            }

            override fun onAdFailedToShowFullScreenContent() {
                super.onAdFailedToShowFullScreenContent()
                val time = RemoteConfigHelper.getInstance().get_config_long(activity, Constants.RemoteKeys.interval_inter_all) * 1000
                if (time > 0) Admob.getInstance().setTimeInterval(time, false)
            }
        })
    }

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
        nativeManager.timeOutCallAds = 12000
        nativeManager.setIntervalReloadNative(
            RemoteConfigHelper.getInstance().get_config_long(activity, Constants.RemoteKeys.interval_reload_native) * 1000
        )
        nativeManager.setAlwaysReloadOnResume(true)
        return nativeManager

    }

    fun loadNativeBanner(
        activity: AppCompatActivity,
        frAds: FrameLayout,
        remoteKey: String = Constants.RemoteKeys.native_banner,
        adsKey: String = Constants.RemoteKeys.native_banner,
        lifecycleOwner: LifecycleOwner,
        callBack: NativeCallback? = null
    ): NativeManager? {
        val idLayoutShimmer = R.layout.shimmer_native_super_small
        val idLayoutNative = R.layout.native_small_ads_meta
        val idNativeMeta = R.layout.native_small_ads_meta

        val nativeBuilder = NativeBuilder(activity, frAds, idLayoutShimmer, idLayoutNative, idNativeMeta, true)
        nativeBuilder.setListIdAdMain(AdmobApi.getInstance().getListIDByName(adsKey))
        //set secondary list id ads if load double native
        nativeBuilder.setListIdAdSecondary(AdmobApi.getInstance().getListIDByName(adsKey))
        if (callBack != null) {
            nativeBuilder.callback = callBack
        }
        val nativeManager = NativeManager(activity, lifecycleOwner, nativeBuilder, remoteKey, remoteKey)
        nativeManager.timeOutCallAds = 12000
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
            type = NativeType.NATIVE_SMALL_ABOVE
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