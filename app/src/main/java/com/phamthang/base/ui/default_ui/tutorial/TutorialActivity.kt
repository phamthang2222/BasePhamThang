package com.phamthang.base.ui.default_ui.tutorial

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.vifarmer.ads.lib.Utils.EventTrackingHelper
import com.vifarmer.ads.lib.Utils.RemoteConfigHelper
import com.vifarmer.ads.lib.admob.Admob
import com.vifarmer.ads.lib.admob.AdmobApi
import com.google.android.gms.ads.interstitial.InterstitialAdPreloader
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.phamthang.base.base.BaseActivity
import com.phamthang.base.extention.gone
import com.phamthang.base.extention.tap
import com.phamthang.base.extention.visible
import com.phamthang.base.ui.feature.home.HomeActivity
import com.phamthang.base.utils.Constants
import com.phamthang.base.R
import com.phamthang.base.ads.AdsHelper
import com.phamthang.base.ads.NativeType
import com.phamthang.base.base.CommonVM
import com.phamthang.base.databinding.ActivityTutorialBinding
import com.phamthang.base.extention.setupCtaZoomAnimation
import com.phamthang.base.rate.showRate
import com.phamthang.base.ui.default_ui.language.LanguageStartActivity
import com.phamthang.base.ui.default_ui.permission.PermissionActivity
import com.phamthang.base.ui.default_ui.wellcome.WelcomeActivity
import com.vifarmer.ads.lib.callback.InterCallback
import com.vifarmer.ads.lib.organic.TechManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TutorialActivity : BaseActivity<CommonVM, ActivityTutorialBinding>() {
    private var introAdapter: IntroAdapter? = null
    private val mHelpGuid: ArrayList<IntroModel> = ArrayList()
    private var countOpenSplash = 1L
    private var isFirst = true
    private var isPause = false
    private var interstitialAdIntro: InterstitialAd? = null
    private var isPreloadInterIntro = false

    companion object {
        var isLogEventOnboardOpen = false
        var nativeAdFullScreen: NativeAd? = null
        var nativeAdFullScreen2: NativeAd? = null
        var nativeAdFullScreen1: NativeAd? = null
        var nativeAdFullScreen12: NativeAd? = null
    }

    override fun onResume() {
        super.onResume()
        isPause = false
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isPause) {
                showNativeIntroPreloadAtSplash()
            }
        }, 1000)
    }

    override fun onPause() {
        super.onPause()
        isPause = true
    }

    private fun showNativeIntroPreloadAtSplash() {
        if (LanguageStartActivity.Companion.nativeIntroPreload != null && !LanguageStartActivity.Companion.isShowNativeIntroPreloadAtSplash && !TechManager.getInstance()
                .isTech(this)
        ) {
            val adView: NativeAdView = layoutInflater.inflate(
                R.layout.native_small_ads_with_button_above,
                binding.frAds,
                false
            ) as NativeAdView
            binding.frAds.addView(adView)
            Admob.getInstance()
                .populateNativeAdView(LanguageStartActivity.Companion.nativeIntroPreload, adView)
        }
    }

    override fun createViewModel(): Class<CommonVM> = CommonVM::class.java
    override fun getViewDataBinding(): ActivityTutorialBinding =
        ActivityTutorialBinding.inflate(layoutInflater)

    override fun initView() {
        enableEdgeToEdge()
        EventTrackingHelper.logEvent(this@TutorialActivity, "onboard_open")
        RemoteConfigHelper.getInstance().set_config_long(
            this@TutorialActivity,
            "onboard_open",
            RemoteConfigHelper.getInstance()
                .get_config_long(this@TutorialActivity, "onboard_open") + 1
        )

        loadNativeIntro()
    }

    override fun bindViewModel() {
        countOpenSplash = RemoteConfigHelper.getInstance().get_config_long(this, "countOpenSplash")
        if (!isLogEventOnboardOpen) {
            if (countOpenSplash <= 10) {
                EventTrackingHelper.logEvent(this, "onboard_open" + "_${countOpenSplash}")
            }
            isLogEventOnboardOpen = true
        }
        mHelpGuid.add(
            IntroModel(
                1,
                R.drawable.img_intro_1,
                getString(R.string.title_intro_1),
                getString(R.string.content_intro_1)
            )
        )
        if (Admob.getInstance().checkCondition(this, Constants.RemoteKeys.native_intro_full) ||
            Admob.getInstance().checkCondition(this, Constants.RemoteKeys.native_intro_full_2)
        ) {
            mHelpGuid.add(IntroModel(99, -1, "ads", "ads"))
        }
        mHelpGuid.add(
            IntroModel(
                2,
                R.drawable.img_intro_2,
                getString(R.string.title_intro_2),
                getString(R.string.content_intro_2)
            )
        )
        mHelpGuid.add(
            IntroModel(
                3,
                R.drawable.img_intro_3,
                getString(R.string.title_intro_3),
                getString(R.string.content_intro_3)
            )
        )

        if (Admob.getInstance().checkCondition(this, Constants.RemoteKeys.native_intro_full1) ||
            Admob.getInstance().checkCondition(this, Constants.RemoteKeys.native_intro_full1_2)
        ) {
            mHelpGuid.add(IntroModel(99, -1, "ads1", "ads1"))
        }
        mHelpGuid.add(
            IntroModel(
                4,
                R.drawable.img_intro_4,
                getString(R.string.title_intro_4),
                getString(R.string.content_intro_4)
            )
        )

        introAdapter = IntroAdapter(this, mHelpGuid) {
            binding.viewPager2.currentItem++
        }
        binding.viewPager2.adapter = introAdapter
        binding.viewPager2.clipToPadding = false
        binding.viewPager2.clipChildren = false
        binding.viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_ALWAYS
        setupIndicators(binding.viewPager2.adapter?.itemCount ?: 0)

        binding.viewPager2.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            @SuppressLint("InvalidAnalyticsName", "UseCompatLoadingForDrawables")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                logEvent(position)

                if (isFirst) {
                    isFirst = false
                    return
                }
                setCurrentIndicator(position)
                updateAdLayoutVisibility(position)
            }
        })

        binding.tvNext.tap {

            if (mHelpGuid[binding.viewPager2.currentItem].position <= 4) {
                EventTrackingHelper.logEvent(
                    this,
                    "onboarding${mHelpGuid[binding.viewPager2.currentItem].position}_next_click"
                )
            }

            when (binding.viewPager2.currentItem) {
                (mHelpGuid.size - 1) -> {
                    gotoNextScreen()
                }

                else -> {
                    binding.viewPager2.currentItem += 1
                }
            }
        }

    }

    private fun logEvent(position: Int) {
        EventTrackingHelper.logEvent(this, "onboarding${position + 1}_view")
    }

    override fun onBackPressCustom() {
        finish()
    }

    private fun gotoNextScreen() {
        EventTrackingHelper.logEvent(this, Constants.TrackingKeys.onboarding_next_click)
        if (countOpenSplash <= 10) {
            EventTrackingHelper.logEvent(this, "onboarding_next_click_$countOpenSplash")
        }
        val ratingAppear = sharePrefApp.countRatingAppear
        if (sharePrefApp.isRated.not() && (ratingAppear == 2 || ratingAppear == 5 || ratingAppear == 9)) {
            showRate(
                sharePrefApp = sharePrefApp,
                onSuccess = {
                    showInter()
                },
                onDismiss = {
                    showInter()
                }
            )
        } else {
            showInter()
        }

    }

    private fun showInter() {
        if (interstitialAdIntro!=null) {
            Admob.getInstance().showInterAds(
                this,
                interstitialAdIntro,
                object : InterCallback() {
                    override fun onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent()
                    }

                    override fun onAdFailedToShowFullScreenContent() {
                        super.onAdFailedToShowFullScreenContent()
                    }

                    override fun onNextAction() {
                        super.onNextAction()
                        startNextAct()
                    }
                },
                true,
                Constants.RemoteKeys.inter_intro,
            )
        } else {
           AdsHelper.loadAndShowInter(this, Constants.AdsKey.inter_intro, Constants.RemoteKeys.inter_intro) {
                startNextAct()
            }
        }
    }

    private fun startNextAct() {
        val openHome = sharePrefApp.isFirstOpenApp
        if (!openHome) {
            showActivity(HomeActivity::class.java, null)
            finishAffinity()
        } else {
            showActivity(PermissionActivity::class.java, null)
            finishAffinity()
        }

    }

    private fun setupIndicators(count: Int) {
        binding.llCircle.removeAllViews()
        val indicators = arrayOfNulls<ImageView>(count)
        val margin = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 3f, resources.displayMetrics
        ).toInt()
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(margin, 0, margin, 0)

        for (i in 0 until count) {
            indicators[i] = ImageView(this).apply {
                setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_indi_unselect))
                layoutParams = params
            }
            binding.llCircle.addView(indicators[i])
        }
        setCurrentIndicator(0)
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = binding.llCircle.childCount
        for (i in 0 until childCount) {
            val imageView = binding.llCircle.getChildAt(i) as ImageView
            imageView.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    if (i == index) R.drawable.ic_indi_select else R.drawable.ic_indi_unselect
                )
            )
        }
    }

    private fun updateAdLayoutVisibility(position: Int) {
        /*  binding.tvNext.text =
              if (position == mHelpGuid.size - 1) getString(R.string.start) else getString(R.string._next)*/

        if (mHelpGuid[position].position == 99) {
            binding.llBottom.gone()
            binding.rlBottom.gone()
        } else {
            binding.llBottom.visible()
            binding.rlBottom.visible()
        }
        if (mHelpGuid[position].position == 2) {
            if (interstitialAdIntro==null) {
                preloadInterIntro()
            }
        }
    }

    private fun preloadInterIntro() {
        if(isPreloadInterIntro){
            return
        }
        isPreloadInterIntro = true
        Admob.getInstance().loadInterAds(
            this,
            AdmobApi.getInstance().getListIDByName(Constants.RemoteKeys.inter_intro),
            object : InterCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd?) {
                    super.onAdLoaded(interstitialAd)
                    interstitialAdIntro = interstitialAd
                }

                override fun onAdFailedToLoad() {
                    super.onAdFailedToLoad()
                }
            },
            Constants.RemoteKeys.inter_intro
        )
    }


    private fun loadNativeIntro() {
        binding.frAds.setupCtaZoomAnimation()
        AdsHelper.loadNative(
            activity = this,
            lifecycleOwner = this,
            frAds = binding.frAds,
            remoteKeyMain = Constants.RemoteKeys.native_intro,
            remoteKeySecondary = Constants.RemoteKeys.native_intro_2,
            type = NativeType.NATIVE_SMALL_ABOVE
        )
    }


}