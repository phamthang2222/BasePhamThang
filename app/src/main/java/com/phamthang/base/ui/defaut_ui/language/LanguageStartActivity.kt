package com.phamthang.base.ui.defaut_ui.language

import android.content.Context
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
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
import com.phamthang.base.utils.Constants
import com.phamthang.base.utils.DataProject
import com.phamthang.base.utils.SystemUtil
import com.phamthang.base.R
import com.phamthang.base.ads.AdsHelper
import com.phamthang.base.ads.NativeType
import com.phamthang.base.databinding.ActivityLanguageBinding
import com.phamthang.base.extention.getLocalizedString
import com.phamthang.base.ui.defaut_ui.language.adapter.LanguageAdapter
import com.phamthang.base.ui.defaut_ui.language.model.LanguageModel
import com.phamthang.base.ui.defaut_ui.language.model.LanguageSubModel
import com.phamthang.base.ui.defaut_ui.splash.SplashActivity
import com.phamthang.base.ui.defaut_ui.splash.SplashActivity.Companion.isShowNativeLanguageClickPreloadAtSplash
import com.phamthang.base.ui.defaut_ui.splash.SplashActivity.Companion.nativeLanguageClickPreload
import com.phamthang.base.ui.defaut_ui.splash.SplashActivity.Companion.nativeLanguageMainPreload
import com.phamthang.base.ui.defaut_ui.tutorial.TutorialActivity
import com.phamthang.base.utils.Constants.AdsKey.inter_splash
import com.phamthang.base.utils.Constants.AdsKey.native_intro
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import androidx.core.content.edit


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

        val nativeManager = AdsHelper.loadNative(
            activity = this,
            lifecycleOwner = this,
            frAds = binding.frAds,
            remoteKeyMain = Constants.RemoteKeys.native_language,
            remoteKeySecondary = Constants.RemoteKeys.native_language_2,
            type = NativeType.NATIVE_LARGE_ABOVE
        )

        adapter = LanguageAdapter(
            setLanguageDefault(),
            onClickSubLanguage = {
                logDebug(message = "ChekClickLFO: onClickSubLanguage: ${it.languageName}-${it.isoLanguage} ")
                EventTrackingHelper.logEvent(this, Constants.TrackingKeys.language_fo_choose)
                if (!isChooseLanguage) {
                    if (countOpenSplash <= 10) {
                        EventTrackingHelper.logEvent(this, Constants.TrackingKeys.language_fo_choose + "_${countOpenSplash}")
                    }
                }
                isChooseLanguage = true
                binding.ivDone.setEnableState(true, R.drawable.ic_tick_off, R.drawable.ic_tick_on)
                model = it
                translate((model as LanguageSubModel).isoLanguage)
            },
            onClickDropDown = {
                nativeManager.cancelAutoReloadNative()
                showNativeLanguageClickPreloadAtSplash()
            },
            onClickSingleLanguage = {
                binding.ivDone.setEnableState(true, R.drawable.ic_tick_off, R.drawable.ic_tick_on)
                model = it
                translate((model as LanguageModel).isoLanguage)
                nativeManager.cancelAutoReloadNative()
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
        if (nativeLanguageClickPreload != null &&
            !isShowNativeLanguageClickPreloadAtSplash &&
            Admob.getInstance().checkCondition(this, Constants.RemoteKeys.native_click)
        ) {
            val adView: NativeAdView = layoutInflater.inflate(R.layout.native_large_ads_with_button_above, binding.frAds, false) as NativeAdView
            binding.frAds.addView(adView)
            Admob.getInstance().populateNativeAdView(nativeLanguageClickPreload, adView)
        }
    }

    fun ivDone(v: View) {
        binding.rlContent.isVisible = false
        binding.llApplyLanguage.isVisible = true
        preloadANativeMainIntro()
        lifecycleScope.launch {
            delay(5000L)
            if (
                Admob.getInstance().checkCondition(this@LanguageStartActivity, Constants.AdsKey.inter_splash) &&
                !TechManager.getInstance().isTech(this@LanguageStartActivity) &&
                Admob.getInstance().interstitialAdSplashHigh != null
            ) {
                Admob.getInstance().showInterAds(this@LanguageStartActivity, Admob.getInstance().interstitialAdSplashHigh, object : InterCallback() {
                    override fun onNextAction() {
                        super.onNextAction()
                        startNextAct()
                    }
                }, false, Constants.RemoteKeys.inter_splash)
            } else if (
                Admob.getInstance().checkCondition(this@LanguageStartActivity, inter_splash) &&
                !TechManager.getInstance().isTech(this@LanguageStartActivity) &&
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
    }


    private fun startNextAct() {
        EventTrackingHelper.logEvent(this, Constants.TrackingKeys.language_fo_save_click)
        if (countOpenSplash < 11) EventTrackingHelper.logEvent(this, "language_fo_save_click_$countOpenSplash")
        getSharedPreferences("MY_PRE", MODE_PRIVATE).edit { putBoolean("openLanguage", true) }
        if (model is LanguageSubModel) {
            SystemUtil.setLanguage(this, (model as LanguageSubModel).isoLanguage, (model as LanguageSubModel).languageName)
        } else {
            SystemUtil.setLanguage(this, (model as LanguageModel).isoLanguage, (model as LanguageModel).languageName)
        }
        SystemUtil.setLocale(this)
        showActivity(TutorialActivity::class.java, null)
        finish()
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

    private fun translate(code: String) {
        val isArabic = code == "ar"
        window.decorView.layoutDirection = if (isArabic) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR

        binding.tvHeader.text = getLocalizedString(code, R.string.please_select_language_to_continue)
        binding.tvLanguage.text = getLocalizedString(code, R.string.language)
        binding.tvTitleApply.text = getLocalizedString(code, R.string.title_apply_lfo)
        binding.tvContentApply.text = getLocalizedString(code, R.string.apply_lfo)
    }
}