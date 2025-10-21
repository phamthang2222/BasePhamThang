package com.phamthang.base.ui.language

import android.content.Context
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.amazic.library.Utils.EventTrackingHelper
import com.amazic.library.Utils.RemoteConfigHelper
import com.amazic.library.ads.admob.Admob
import com.amazic.library.ads.callback.InterCallback
import com.amazic.library.organic.TechManager
import com.phamthang.base.extention.setEnableState
import com.google.android.gms.ads.nativead.NativeAd
import com.phamthang.base.base.BaseActivity
import com.phamthang.base.extention.tap
import com.phamthang.base.extention.visible
import com.phamthang.base.share_pref.SharedPrefManager
import com.phamthang.base.ui.splash.SplashActivity
import com.phamthang.base.ui.tutorial.TutorialActivity
import com.phamthang.base.utils.Constants
import com.phamthang.base.utils.SystemUtil
import com.phamthang.base.R
import com.phamthang.base.databinding.ActivityLanguageBinding
import java.util.Locale

class LanguageStartActivity : BaseActivity<LanguageViewModel, ActivityLanguageBinding>(),
    IClickLanguage {
    private var firstClick = true
    private var adapter: LanguageAdapter? = null
    private var model: LanguageModel = LanguageModel()
    private var isPause = false
    private var countOpenSplash = 1L
    private var isChooseLanguage = false

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

        binding.ivDone.setEnableState(false,0f)
        adapter = LanguageAdapter(
            this,
            setLanguageDefault(),
            this,
            true,
        )
        binding.rclLanguage.adapter = adapter
        binding.ivBack.tap {
            onBackPressCustom()
        }

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

    private fun setLanguageDefault(): List<LanguageModel> {
        val lists: MutableList<LanguageModel> = ArrayList()
        val key: String = SystemUtil.getPreLanguage(this)
        Log.d("CHECK_systemLang", " key: ${key}")


        lists.add(LanguageModel("中文 (简体)", "zh-rCN", false, R.drawable.flag_china))
        lists.add(LanguageModel("中文 (繁體) ", "zh-rTW", false, R.drawable.flag_china))
        lists.add(LanguageModel("हिन्दी", "hi", false, R.drawable.flag_hindi))
        lists.add(LanguageModel("English", "en", false, R.drawable.flag_en))
        lists.add(LanguageModel("Portuguese (Brazil)", "pt-rBR", false, R.drawable.flag_port_brazil))
        lists.add(LanguageModel("Portuguese (Portugal)", "pt-rPT", false, R.drawable.flag_port_portu))
        lists.add(LanguageModel("Français", "fr", false, R.drawable.flag_fra))
        lists.add(LanguageModel("Español", "es", false, R.drawable.flag_span))
        lists.add(LanguageModel("عربي", "ar", false, R.drawable.flag_a_rap))
        lists.add(LanguageModel("বাংলা ", "bn", false, R.drawable.flag_bengali))
        lists.add(LanguageModel("Русский ", "ru", false, R.drawable.flag_russia))
        lists.add(LanguageModel("Deutsch", "de", false, R.drawable.flag_ger))
        lists.add(LanguageModel("日本語", "ja", false, R.drawable.flag_japan))
        lists.add(LanguageModel("Türkçe ", "tr", false, R.drawable.flag_turkiye))
        lists.add(LanguageModel("한국인 ", "ko", false, R.drawable.flag_korean))
        lists.add(LanguageModel("Indonesian", "in", false, R.drawable.flag_indonesia))
        val selectedLanguage = SharedPrefManager.getBoolean(SharedPrefManager.SELECTED_LANGUAGE, false)
        if (selectedLanguage) {
            val systemLangModel = lists.find { it.isoLanguage.contains(key) }
            systemLangModel?.let {
                it.isCheck = true
                lists.remove(it)
                lists.add(0, it)
            }
            binding.ivDone.setEnableState(true)

        }
        return lists
    }

    fun ivDone(v: View) {
        SharedPrefManager.saveBoolean(SharedPrefManager.SELECTED_LANGUAGE, true)
        val editor = getSharedPreferences("MY_PRE", MODE_PRIVATE).edit()
        editor.putBoolean("openLanguage", true)
        editor.apply()
        SystemUtil.setPreLanguage(this@LanguageStartActivity, model.isoLanguage)
        SystemUtil.setLocale(this)
        startWithInter()
    }

    private fun startWithInter() {
        EventTrackingHelper.logEvent(this, Constants.TrackingKeys.language_fo_save_click)
        if (countOpenSplash <= 10) {
            EventTrackingHelper.logEvent(this, Constants.TrackingKeys.language_fo_save_click + "_${countOpenSplash}")
        }

        if (Admob.getInstance().checkCondition(this, Constants.RemoteKeys.inter_splash) &&
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
        showActivity(TutorialActivity::class.java, null)
        finish()
    }

    override fun onClick(data: LanguageModel) {
        binding.ivDone.setEnableState(true)

        EventTrackingHelper.logEvent(this, Constants.TrackingKeys.language_fo_choose)
        if (!isChooseLanguage) {
            nativeManager?.reloadAdNow()
            if (countOpenSplash <= 10) {
                EventTrackingHelper.logEvent(this, Constants.TrackingKeys.language_fo_choose + "_${countOpenSplash}")
            }
        }

        model = data
        val isArabic = model.isoLanguage == "ar"
        window.decorView.layoutDirection = if (isArabic) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR

        SystemUtil.setLocale(this)
        binding.tvHeader.text = getLocalizedString(this, model.isoLanguage, R.string.please_select_language_to_continue)
        binding.tvLanguage.text = getLocalizedString(this, model.isoLanguage, R.string.language)
        binding.ivDone.visible()
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
    private fun loadNativeLanguage() {
        /* loadNative(
             binding.frAds,
             Constants.RemoteKeys.native_language,
             Constants.RemoteKeys.native_language_2,
             Constants.AdsKey.native_language,
             Constants.AdsKey.native_language_2,
             R.layout.native_large_ads_with_button_above,
             R.layout.shimmer_native_large_with_button_above,
         )*/
    }

    private fun showNativeLanguagePreloadAtSplash() {
         /*if (SplashActivity.Companion.nativeLanguagePreload != null && !SplashActivity.Companion.isShowNativeLanguagePreloadAtSplash) {
             val adView: NativeAdView = layoutInflater.inflate(R.layout.native_large_ads_with_button_above, binding.frAds, false) as NativeAdView
             binding.frAds.addView(adView)
             Admob.getInstance().populateNativeAdView(SplashActivity.Companion.nativeLanguagePreload, adView)
         }*/
    }
}