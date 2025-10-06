package com.phamthang.reverseaudio.ui.tutorial

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
import com.amazic.library.Utils.EventTrackingHelper
import com.amazic.library.Utils.RemoteConfigHelper
import com.amazic.library.ads.admob.Admob
import com.amazic.library.ads.admob.AdmobApi
import com.amazic.library.ads.callback.NativeCallback
import com.amazic.library.organic.TechManager
import com.phamthang.reverseaudio.extention.logDebug
import com.google.android.gms.ads.LoadAdError

import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.play.core.review.ReviewManagerFactory
import com.phamthang.reverseaudio.R
import com.phamthang.reverseaudio.databinding.ActivityTutorialBinding
import com.phamthang.reverseaudio.base.BaseActivity
import com.phamthang.reverseaudio.extention.gone
import com.phamthang.reverseaudio.extention.tap
import com.phamthang.reverseaudio.extention.visible
import com.phamthang.reverseaudio.rate.RatingDialog
import com.phamthang.reverseaudio.rate.SharePrefUtils
import com.phamthang.reverseaudio.share_pref.SharedPrefManager
import com.phamthang.reverseaudio.ui.home.HomeActivity
import com.phamthang.reverseaudio.ui.wellcome.WelcomeActivity
import com.phamthang.reverseaudio.utils.Constants

class TutorialActivity : BaseActivity<TutorialVM, ActivityTutorialBinding>() {
    private var introAdapter: IntroAdapter? = null
    private val mHelpGuid: ArrayList<IntroModel> = ArrayList()
    private var countOpenSplash = 1L
    private var isFirst = true
    private var isPause = false

    companion object {
        var isLogEventOnboardOpen = false
        var nativeAd: NativeAd? = null
        var nativeAd1: NativeAd? = null
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
       /* if (LanguageStartActivity.Companion.nativeIntroPreload != null && !LanguageStartActivity.Companion.isShowNativeIntroPreloadAtSplash && !TechManager.getInstance().isTech(this)) {
            val adView: NativeAdView = layoutInflater.inflate(
                R.layout.native_small_ads_with_button_above,
                binding.frAds,
                false
            ) as NativeAdView
            binding.frAds.addView(adView)
            Admob.getInstance().populateNativeAdView(LanguageStartActivity.Companion.nativeIntroPreload, adView)
        }*/
    }

    override fun createViewModel(): Class<TutorialVM> = TutorialVM::class.java
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
       /* if (Admob.getInstance().checkCondition(this, Constants.RemoteKeys.native_intro_full) || Admob.getInstance().checkCondition(this, Constants.RemoteKeys.native_intro_full_2)) {
            mHelpGuid.add(IntroModel(99, -1, "ads", "ads"))
        }*/
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
    /*    if (Admob.getInstance().checkCondition(this, Constants.RemoteKeys.native_intro_full1) || Admob.getInstance().checkCondition(this, Constants.RemoteKeys.native_intro_full1_2)) {
            mHelpGuid.add(IntroModel(99, -1, "ads1", "ads1"))
        }*/
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
        binding.viewPager2.offscreenPageLimit = 4
        binding.viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_ALWAYS
        //animation
       /* val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(100))
        compositePageTransformer.addTransformer { view, position ->
            val r = 1 - abs(position)
            view.scaleY = 0.8f + r * 0.2f
            val absPosition = abs(position)
            view.alpha = 1.0f - (1.0f - 0.3f) * absPosition
        }
        binding.viewPager2.setPageTransformer(compositePageTransformer)*/
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
        logDebug(message = "xdadas_onboarding${position}_view")
        EventTrackingHelper.logEvent(this, "onboarding${position+1}_view")
    }

    override fun onBackPressCustom() {
        finish()
    }

    private fun gotoNextScreen() {
        EventTrackingHelper.logEvent(this, Constants.TrackingKeys.onboarding_next_click)
        if(countOpenSplash<=10){
            EventTrackingHelper.logEvent(this,"onboarding_next_click_$countOpenSplash")
        }
        val ratingAppear = SharedPrefManager.getInt(Constants.KeyApp.RATING_APPEAR,1)
        if(!SharePrefUtils.isRated(this) && (ratingAppear==2 || ratingAppear==5 || ratingAppear==9)){
            /*showRatingDialog{
                showInter()
            }*/
            showInter()
        }else{
            showInter()
        }
    }

    private fun showInter() {
       /* loadAndShowInter(
            Constants.AdsKey.inter_intro,
            Constants.RemoteKeys.inter_intro
        ) {
            startNextAct()
        }*/
        startNextAct()
    }

    private fun startNextAct() {
        val openHome = SharedPrefManager.getBoolean(SharedPrefManager.FIRST_OPEN_APP, true)
        if (!openHome) {
            showActivity(HomeActivity::class.java, null)
            finishAffinity()
        } else {
            showActivity(WelcomeActivity::class.java, null)
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
        binding.tvNext.text =
            if (position == mHelpGuid.size - 1) getString(R.string.start) else getString(R.string._continue)

        if (mHelpGuid[position].position == 99) {
            binding.llBottom.gone()
            binding.rlBottom.gone()
        } else {
            binding.llBottom.visible()
            binding.rlBottom.visible()
        }
    }

    private fun loadNativeIntro() {
       /* loadNative(
            binding.frAds,
            Constants.RemoteKeys.native_intro,
            Constants.RemoteKeys.native_intro_2,
            Constants.AdsKey.native_intro,
            Constants.AdsKey.native_intro_2,
            R.layout.native_small_ads_with_button_above,
            R.layout.shimmer_native_small_with_button_above
        )
        loadNativeIntroFull(Constants.AdsKey.native_intro_full, Constants.RemoteKeys.native_intro_full) //main
        loadNativeIntroFull(Constants.AdsKey.native_intro_full_2, Constants.RemoteKeys.native_intro_full_2) //second
        loadNativeIntroFull1(Constants.AdsKey.native_intro_full1, Constants.RemoteKeys.native_intro_full1) //main
        loadNativeIntroFull1(Constants.AdsKey.native_intro_full1_2, Constants.RemoteKeys.native_intro_full1_2) //main*/
    }


    private fun loadNativeIntroFull(adsKey: String, remoteKey: String){
        if (Admob.getInstance().checkCondition(this, remoteKey)) {
            Admob.getInstance().loadNativeAds(
                this,
                AdmobApi.getInstance().getListIDByName(adsKey),
                object : NativeCallback() {
                    override fun onNativeAdLoaded(nativeAd: NativeAd) {
                        super.onNativeAdLoaded(nativeAd)
                        if(remoteKey == Constants.RemoteKeys.native_intro_full){
                            introAdapter?.setNativeAds(nativeAd, 1)
                        }else{
                            introAdapter?.setNativeAds2(nativeAd, 1)
                        }
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError?) {
                        super.onAdFailedToLoad(loadAdError)
                    }
                },
                remoteKey
            )
        }

    }
    private fun loadNativeIntroFull1(adsKey: String, remoteKey: String){
        if (Admob.getInstance().checkCondition(this, remoteKey)) {
            Admob.getInstance().loadNativeAds(
                this,
                AdmobApi.getInstance().getListIDByName(adsKey),
                object : NativeCallback() {
                    override fun onNativeAdLoaded(nativeAd: NativeAd) {
                        super.onNativeAdLoaded(nativeAd)
                        logDebug(message = "nativeAd :$nativeAd")
                        if (Admob.getInstance().checkCondition(this@TutorialActivity, Constants.RemoteKeys.native_intro_full) || Admob.getInstance().checkCondition(this@TutorialActivity, Constants.RemoteKeys.native_intro_full_2)) {
                            if(remoteKey == Constants.RemoteKeys.native_intro_full1){
                                introAdapter?.setNativeAds1(nativeAd, 4)
                            }else{
                                introAdapter?.setNativeAds12(nativeAd, 4)
                            }
                        } else {
                            if(remoteKey == Constants.RemoteKeys.native_intro_full1){
                                introAdapter?.setNativeAds1(nativeAd, 3)
                            }else{
                                introAdapter?.setNativeAds12(nativeAd, 3)
                            }
                        }
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError?) {
                        super.onAdFailedToLoad(loadAdError)
                    }
                },
                remoteKey
            )
        }

    }

    private fun showRatingDialog(onRating: () -> Unit) {
        val ratingDialog = RatingDialog(this)
        ratingDialog.setCancelable(false)
        ratingDialog.init(this, object : RatingDialog.OnPress {
            override fun send(rate: Float) {
                val manager = ReviewManagerFactory.create(this@TutorialActivity)
                manager.requestReviewFlow().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val reviewInfo = task.result
                        manager.launchReviewFlow(this@TutorialActivity, reviewInfo)
                        SharePrefUtils.forceRated(this@TutorialActivity)
                        logDebug(tagSpecial = "showRatingDialog", message = "send")
                        onRating()
                    }
                }
            }

            override fun rating(rate: Float) {
                SharePrefUtils.forceRated(this@TutorialActivity)
                onRating()
                logDebug(tagSpecial = "showRatingDialog",message = "rating")
            }

            override fun cancel() {
                onRating()
                logDebug(tagSpecial = "showRatingDialog",message = "cancel")
            }

            override fun later() {
                onRating()
                logDebug(tagSpecial = "showRatingDialog",message = "later")
            }
        })
        ratingDialog.show()
    }


}