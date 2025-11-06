package com.phamthang.base.ui.language

import android.content.Context
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.view.View
import com.amazic.library.Utils.EventTrackingHelper
import com.amazic.library.Utils.RemoteConfigHelper
import com.amazic.library.ads.admob.Admob
import com.amazic.library.ads.admob.AdmobApi
import com.amazic.library.ads.callback.BannerCallback
import com.amazic.library.ads.callback.InterCallback
import com.amazic.library.ads.callback.NativeCallback
import com.amazic.library.organic.TechManager
import com.phamthang.base.extention.setEnableState
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.phamthang.base.base.BaseActivity
import com.phamthang.base.extention.logDebug
import com.phamthang.base.extention.tap
import com.phamthang.base.share_pref.SharedPrefManager
import com.phamthang.base.ui.language.adapter.LanguageAdapter
import com.phamthang.base.ui.language.model.LanguageModel
import com.phamthang.base.ui.language.model.LanguageSubModel
import com.phamthang.base.ui.splash.SplashActivity
import com.phamthang.base.ui.tutorial.TutorialActivity
import com.phamthang.base.utils.Constants
import com.phamthang.base.utils.DataProject
import com.phamthang.base.utils.SystemUtil
import com.phamthang.base.R
import com.phamthang.base.databinding.ActivityLanguageBinding
import com.phamthang.base.ui.splash.SplashActivity.Companion.isShowNativeLanguageClickPreloadAtSplash
import com.phamthang.base.ui.splash.SplashActivity.Companion.nativeLanguageClickPreload
import com.phamthang.base.ui.splash.SplashActivity.Companion.nativeLanguageMainPreload
import com.phamthang.base.utils.Constants.AdsKey.inter_splash
import com.phamthang.base.utils.Constants.AdsKey.native_intro
import java.util.Locale


class LanguageStartActivity : BaseActivity<LanguageViewModel, ActivityLanguageBinding>() {
    private var adapter: LanguageAdapter? = null
    private var model: Any? = null
    private var isChooseLanguage = false
    private var isPause = false
    private val TAG = "LanguageStartActivity"
    private var countOpenSplash = 1L

    companion object {
        var isLogEventLanguageUserView = false
        var nativeIntroPreload: NativeAd? = null
        var isShowNativeIntroPreloadAtSplash = false
    }

    override fun createViewModel(): Class<LanguageViewModel> = LanguageViewModel::class.java

    override fun getViewDataBinding(): ActivityLanguageBinding = ActivityLanguageBinding.inflate(layoutInflater)

    override fun initView() {
        countOpenSplash = RemoteConfigHelper.getInstance().get_config_long(this, "countOpenSplash")
        if (!RemoteConfigHelper.getInstance().get_config(this, "isLogEventLanguageFO")) {
            EventTrackingHelper.logEvent(this, Constants.TrackingKeys.language_fo_open)
            if (countOpenSplash <= 10) {
                EventTrackingHelper.logEvent(this, Constants.TrackingKeys.language_fo_open + "_${countOpenSplash}")
                RemoteConfigHelper.getInstance().set_config(this, "isLogEventLanguageFO", true)
            }
        }

        binding.ivDone.setEnableState(false, R.drawable.ic_tick_off, R.drawable.ic_tick_on)
        binding.rclLanguage.adapter = adapter
        binding.ivBack.tap {
            onBackPressCustom()
        }
        setUpListLang()
        loadBannerSettingForTech()
        loadNativeLanguage()
    }

    override fun bindViewModel() {

    }

    override fun onBackPressCustom() {
        finishAffinity()
    }

    override fun onResume() {
        super.onResume()
        isPause = false
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isPause) {
                showNativeLanguagePreloadAtSplash()
            }
            if (SplashActivity.Companion.isShowSplashAds) {
                if (SplashActivity.Companion.isCloseSplashAds) {
                    if (countOpenSplash <= 10 && !isLogEventLanguageUserView && !isPause) {
                        EventTrackingHelper.logEvent(this@LanguageStartActivity, "language_user_view" + "_${countOpenSplash}")
                        EventTrackingHelper.logEvent(this@LanguageStartActivity, "language_user_view")
                        isLogEventLanguageUserView = true
                    }
                }
            } else {
                if (countOpenSplash <= 10 && !isLogEventLanguageUserView && !isPause) {
                    EventTrackingHelper.logEvent(this@LanguageStartActivity, "language_user_view" + "_${countOpenSplash}")
                    EventTrackingHelper.logEvent(this@LanguageStartActivity, "language_user_view")
                    isLogEventLanguageUserView = true
                }
            }
        }, 1000)
    }

    override fun onPause() {
        super.onPause()
        isPause = true
    }

    private fun setUpListLang() {
        val nativeManager = loadNative(
            frAds = binding.frAds,
            remoteKeyMain = Constants.RemoteKeys.native_language,
            remoteKeySecondary = Constants.RemoteKeys.native_language_2,
            adsKeyMain = Constants.AdsKey.native_language,
            adsKeySecondary = Constants.AdsKey.native_language_2,
            idLayoutShimmer = R.layout.shimmer_native_large_with_button_above,
            idLayoutNative = R.layout.native_large_ads_with_button_above,
        )

        adapter = LanguageAdapter(
            setLanguageDefault(),
            onClickSubLanguage = {
                logDebug(message = "ChekClickLFO: onClickSubLanguage: ${it.languageName}-${it.isoLanguage} ")
                binding.ivDone.alpha = 1f
                binding.ivDone.isEnabled = true
                EventTrackingHelper.logEvent(this, Constants.TrackingKeys.language_fo_choose)
                if (!isChooseLanguage) {
                    if (countOpenSplash <= 10) {
                        EventTrackingHelper.logEvent(this, Constants.TrackingKeys.language_fo_choose + "_${countOpenSplash}")
                    }
                }
                isChooseLanguage = true
                binding.ivDone.setEnableState(true, R.drawable.ic_tick_off, R.drawable.ic_tick_on)
                model = it

                val isArabic = (model as LanguageSubModel).isoLanguage == "ar"
                window.decorView.layoutDirection = if (isArabic) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR

                binding.tvHeader.text = getLocalizedString(this, (model as LanguageSubModel).isoLanguage, R.string.please_select_language_to_continue)
                binding.tvLanguage.text = getLocalizedString(this, (model as LanguageSubModel).isoLanguage, R.string.language)


            },
            onClickDropDown = {
                logDebug(message = "ChekClickLFO: onClickDropDown")
                nativeManager?.cancelAutoReloadNative()
                showNativeLanguageClickPreloadAtSplash()
            },
            onClickSingleLanguage = {
                logDebug(message = "ChekClickLFO: onClickSingleLanguage: ${it.languageName}-${it.isoLanguage}")
                binding.ivDone.setEnableState(true, R.drawable.ic_tick_off, R.drawable.ic_tick_on)
                model = it

                val isArabic = (model as LanguageModel).isoLanguage == "ar"
                window.decorView.layoutDirection = if (isArabic) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR

                binding.tvHeader.text = getLocalizedString(this, (model as LanguageModel).isoLanguage, R.string.please_select_language_to_continue)
                binding.tvLanguage.text = getLocalizedString(this, (model as LanguageModel).isoLanguage, R.string.language)

                nativeManager?.cancelAutoReloadNative()
                showNativeLanguageClickPreloadAtSplash()
            }
        )
        binding.rclLanguage.adapter = adapter
    }

    private fun setLanguageDefault(): List<LanguageModel> {
        val lists: MutableList<LanguageModel> = DataProject.listLanguage()

        return lists
    }

    private fun showNativeLanguageClickPreloadAtSplash() {
        if (nativeLanguageClickPreload != null && !isShowNativeLanguageClickPreloadAtSplash && Admob.getInstance()
                .checkCondition(this, Constants.RemoteKeys.native_language)
        ) {
            val adView: NativeAdView = layoutInflater.inflate(R.layout.native_large_ads_with_button_above, binding.frAds, false) as NativeAdView
            binding.frAds.addView(adView)
            Admob.getInstance().populateNativeAdView(nativeLanguageClickPreload, adView)
        }
    }

    fun ivDone(v: View) {
        /* binding.rlContent.visibility = View.GONE
         binding.llApplyLanguage.visibility = View.VISIBLE*/
        preloadANativeMainIntro()
        if (Admob.getInstance().checkCondition(this, Constants.AdsKey.inter_splash) &&
            !TechManager.getInstance().isTech(this) &&
            Admob.getInstance().interstitialAdSplashHigh != null
        ) {
            Admob.getInstance().showInterAds(this@LanguageStartActivity, Admob.getInstance().interstitialAdSplashHigh, object : InterCallback() {
                override fun onNextAction() {
                    super.onNextAction()
                    startNextAct()
                }
            }, false, Constants.RemoteKeys.inter_splash)
        } else if (Admob.getInstance().checkCondition(this, inter_splash) &&
            !TechManager.getInstance().isTech(this) &&
            Admob.getInstance().interstitialAdSplash != null
        ) {
            Admob.getInstance().showInterAds(this@LanguageStartActivity, Admob.getInstance().interstitialAdSplash, object : InterCallback() {
                override fun onNextAction() {
                    super.onNextAction()
                    startNextAct()
                }
            }, false, Constants.RemoteKeys.inter_splash)
        } else {
            startNextAct()
        }
    }


    private fun startNextAct() {
        EventTrackingHelper.logEvent(this, Constants.TrackingKeys.language_fo_save_click)
        val count = SharedPrefManager.getInt("language_fo_save_click")
        if (count < 11) EventTrackingHelper.logEvent(this, "language_fo_save_click_$count")
        countOpenSplash = RemoteConfigHelper.getInstance().get_config_long(this, "countOpenSplash")

        SharedPrefManager.saveBoolean("isFirstSelectLanguage", true)
        val editor = getSharedPreferences("MY_PRE", MODE_PRIVATE).edit()
        editor.putBoolean("openLanguage", true)
        editor.apply()
        if (model is LanguageSubModel) {
            SystemUtil.setPreLanguage(this@LanguageStartActivity, (model as LanguageSubModel).isoLanguage)
            SystemUtil.setPreLanguageName(this, (model as LanguageSubModel).languageName)
        } else {
            SystemUtil.setPreLanguage(this@LanguageStartActivity, (model as LanguageModel).isoLanguage)
            SystemUtil.setPreLanguageName(this, (model as LanguageModel).languageName)
        }
        SharedPrefManager.saveBoolean("isFirstSelectLanguage", true)
        SystemUtil.setLocale(this)
        showActivity(TutorialActivity::class.java, null)
        finish()
    }


    private fun getLocalizedString(context: Context, languageCode: String, resId: Int): String {
        val localeParts = languageCode.split("-")
        val myLocale = if (localeParts.size > 1) {
            Locale(localeParts[0], localeParts[1])
        } else {
            Locale(languageCode)
        }
        val config = Configuration(context.resources.configuration)
        config.setLocale(myLocale)
        val localizedContext = context.createConfigurationContext(config)
        return localizedContext.resources.getString(resId)
    }

    //ads
    private fun loadBannerSettingForTech() {
        Admob.getInstance().loadBannerAds(
            this,
            AdmobApi.getInstance().getListIDByName(Constants.AdsKey.banner_setting),
            binding.bannerSetting,
            object : BannerCallback() {},
            {},
            Constants.RemoteKeys.banner_setting
        )
    }

    private fun loadNativeLanguage() {
        loadNative(
            binding.frAds,
            Constants.RemoteKeys.native_language,
            Constants.RemoteKeys.native_language_2,
            Constants.AdsKey.native_language,
            Constants.AdsKey.native_language_2,
            R.layout.native_large_ads_with_button_above,
            R.layout.shimmer_native_large_with_button_above,
        )
    }

    private fun showNativeLanguagePreloadAtSplash() {
        if (nativeLanguageMainPreload != null && !SplashActivity.Companion.isShowNativeLanguagePreloadAtSplash) {
            val adView: NativeAdView = layoutInflater.inflate(R.layout.native_large_ads_with_button_above, binding.frAds, false) as NativeAdView
            binding.frAds.addView(adView)
            Admob.getInstance().populateNativeAdView(SplashActivity.Companion.nativeLanguageMainPreload, adView)
        }
    }

    private fun preloadANativeMainIntro() {
        if (!TechManager.getInstance().isTech(this)) {
            Admob.getInstance().loadNativeAds(
                this,
                AdmobApi.getInstance().getListIDByName(native_intro),
                object : NativeCallback() {
                    override fun onNativeAdLoaded(nativeAd: NativeAd?) {
                        super.onNativeAdLoaded(nativeAd)
                        nativeIntroPreload = nativeAd
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        isShowNativeIntroPreloadAtSplash = true
                    }
                }, native_intro
            )
        }
    }
}