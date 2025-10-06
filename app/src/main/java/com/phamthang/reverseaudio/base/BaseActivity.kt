package com.phamthang.reverseaudio.base

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.amazic.library.Utils.RemoteConfigHelper
import com.amazic.library.ads.admob.Admob
import com.amazic.library.ads.admob.AdmobApi
import com.amazic.library.ads.app_open_ads.AppOpenManager
import com.amazic.library.ads.banner_ads.BannerBuilder
import com.amazic.library.ads.banner_ads.BannerManager
import com.amazic.library.ads.callback.InterCallback
import com.amazic.library.ads.callback.RewardedCallback
import com.amazic.library.ads.collapse_banner_ads.CollapseBannerBuilder
import com.amazic.library.ads.collapse_banner_ads.CollapseBannerManager
import com.amazic.library.ads.inter_ads.InterManager
import com.amazic.library.ads.native_ads.NativeBuilder
import com.amazic.library.ads.native_ads.NativeManager
import com.amazic.library.ads.reward_ads.RewardManager
import com.google.android.play.core.install.model.InstallStatus
import com.phamthang.reverseaudio.R
import com.phamthang.reverseaudio.extention.hideNavigation
import com.phamthang.reverseaudio.extention.openAppPermissionSettings
import com.phamthang.reverseaudio.custome_view.LoadingView
import com.phamthang.reverseaudio.extention.tap
import com.phamthang.reverseaudio.ui.no_internet.NetworkReceiver
import com.phamthang.reverseaudio.ui.no_internet.NoInternetActivity
import com.phamthang.reverseaudio.ui.splash.SplashActivity
import com.phamthang.reverseaudio.ui.welcome_back.WelcomeBackActivity
import com.phamthang.reverseaudio.utils.Constants
import com.phamthang.reverseaudio.utils.SystemUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BaseActivity<VM : ViewModel, T : ViewBinding> : AppCompatActivity() {

    abstract fun createViewModel(): Class<VM>
    abstract fun getViewDataBinding(): T
    abstract fun initView()
    abstract fun bindViewModel()
    open fun onBackPressCustom() {}
    lateinit var viewModel: VM
    lateinit var binding: T

    private val TAG = "BaseActivity"
    private var currentApiVersion = 0
    private var networkReceiver: NetworkReceiver? = null
    var nativeManager: NativeManager? = null
    private var  collapseBannerManager : CollapseBannerManager? = null

    private lateinit var loadingDialog: Dialog




    override fun onCreate(savedInstanceState: Bundle?) {
        SystemUtil.setLocale(this)
        hideNavigationBar()
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[createViewModel()]
        networkReceiver = NetworkReceiver {
            if (!it) {
                showActivityCustom(NoInternetActivity::class.java, null)
            } else {
                if (this is NoInternetActivity && intent?.extras?.getString(Constants.ScreenKey.SCREEN) != Constants.ScreenKey.SPLASH_ACTIVITY) {
                    finish()
                } else if (this is NoInternetActivity && intent?.extras?.getString(Constants.ScreenKey.SCREEN) == Constants.ScreenKey.SPLASH_ACTIVITY) {
                    val myIntent = Intent(this, SplashActivity::class.java)
                    myIntent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(myIntent)
                }
            }
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                networkReceiver,
                IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"),
                RECEIVER_EXPORTED
            )
        } else {
            registerReceiver(networkReceiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))
        }
        initView()
        bindViewModel()
        baseView()

    }


    open fun showLoading() {
        if (::loadingDialog.isInitialized.not())
            loadingDialog = LoadingView.Companion.createDialogLoading(this)
        if (loadingDialog.isShowing.not())
            loadingDialog.show()
    }

    open fun hideLoading() {
        if (::loadingDialog.isInitialized && loadingDialog.isShowing)
            loadingDialog.dismiss()
    }


    /*fun loadCollapseBanner(adsKey: String, remoteKey: String) {
        val frContainerAds = findViewById<FrameLayout>(R.id.collapsible_banner_container_view)
        if (frContainerAds != null && Admob.getInstance().checkCondition(this, remoteKey)) {
            val collapseBannerBuilder = CollapseBannerBuilder()
            collapseBannerBuilder.setListId(AdmobApi.getInstance().getListIDByName(adsKey))
            collapseBannerManager = CollapseBannerManager(this, frContainerAds, this, collapseBannerBuilder, remoteKey)
            collapseBannerManager?.setIntervalReloadBanner(
                RemoteConfigHelper.getInstance().get_config_long(this, Constants.RemoteKeys.collap_reload_interval) * 1000
            )
            collapseBannerManager?.setAlwaysReloadOnResume(true)
        }
    }

    fun reloadCollapse(){
        collapseBannerManager?.reloadAdNow()
    }
    private fun loadBannerAds(
        frAds: FrameLayout,
        adsKey: String
    ) {
        val bannerBuilder = BannerBuilder(this, frAds, true)
        bannerBuilder.setListIdAdMain(AdmobApi.getInstance().getListIDByName(adsKey))
        val bannerManager = BannerManager(this, this, bannerBuilder, adsKey)
        bannerManager.setAlwaysReloadOnResume(true)
    }

    fun loadNative(
        frAds: FrameLayout,
        remoteKeyMain: String,
        remoteKeySecondary: String,
        adsKeyMain: String,
        adsKeySecondary: String,
        idLayoutNative: Int,
        idLayoutShimmer: Int,
        idNativeMeta: Int = idLayoutNative
    ) {
        val nativeBuilder = NativeBuilder(this, frAds, idLayoutShimmer, idLayoutNative, idNativeMeta, true)
        nativeBuilder.setListIdAdMain(AdmobApi.getInstance().getListIDByName(adsKeyMain))
        //set secondary list id ads if load double native
        nativeBuilder.setListIdAdSecondary(AdmobApi.getInstance().getListIDByName(adsKeySecondary))
        nativeManager = NativeManager(this, this, nativeBuilder, remoteKeyMain, remoteKeySecondary)
        nativeManager?.timeOutCallAds = 12000
        nativeManager?.setIntervalReloadNative(
            RemoteConfigHelper.getInstance().get_config_long(this, Constants.RemoteKeys.interval_reload_native) * 1000
        )
        nativeManager?.setAlwaysReloadOnResume(true)
    }

    fun loadNativeAll(
        frAds: FrameLayout,
        remoteKeyMain: String = Constants.RemoteKeys.native_all,
        remoteKeySecondary: String = Constants.RemoteKeys.native_all,
        adsKeyMain: String = Constants.AdsKey.native_all,
        adsKeySecondary: String = Constants.AdsKey.native_all,
        idLayoutNative: Int = R.layout.native_large_ads_with_button_above,
        idLayoutShimmer: Int = R.layout.shimmer_native_large_with_button_above,
        idNativeMeta: Int = idLayoutNative
    ) {

        val nativeBuilder = NativeBuilder(this, frAds, idLayoutShimmer, idLayoutNative, idNativeMeta, true)
        nativeBuilder.setListIdAdMain(AdmobApi.getInstance().getListIDByName(adsKeyMain))
        //set secondary list id ads if load double native
        nativeBuilder.setListIdAdSecondary(AdmobApi.getInstance().getListIDByName(adsKeySecondary))
        nativeManager = NativeManager(this, this, nativeBuilder, remoteKeyMain, remoteKeySecondary)
        nativeManager?.timeOutCallAds = 12000
        nativeManager?.setIntervalReloadNative(
            RemoteConfigHelper.getInstance().get_config_long(this, Constants.RemoteKeys.interval_reload_native) * 1000
        )
        nativeManager?.setAlwaysReloadOnResume(true)
    }

    fun loadAndShowInter(
        adsKey: String,
        remoteKey: String,
        onNextAction: () -> Unit
    ) {
        InterManager.loadAndShowInterAds(this, adsKey, remoteKey, object : InterCallback() {
            override fun onNextAction() {
                super.onNextAction()
                onNextAction.invoke()
            }
        })
    }

    fun loadAndShowInterAll(
        adsKey: String = Constants.AdsKey.inter_all,
        remoteKey: String = Constants.RemoteKeys.inter_all,
        onNextAction: () -> Unit
    ) {
        InterManager.loadAndShowInterAds(this, adsKey, remoteKey, object : InterCallback() {
            override fun onNextAction() {
                super.onNextAction()
                onNextAction.invoke()
            }

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                val time = RemoteConfigHelper.getInstance().get_config_long(this@BaseActivity, Constants.RemoteKeys.interval_inter_all) * 1000
                if (time > 0) Admob.getInstance().setTimeInterval(time, false)
            }

            override fun onAdFailedToShowFullScreenContent() {
                super.onAdFailedToShowFullScreenContent()
                val time = RemoteConfigHelper.getInstance().get_config_long(this@BaseActivity, Constants.RemoteKeys.interval_inter_all) * 1000
                if (time > 0) Admob.getInstance().setTimeInterval(time, false)
            }
        })
    }

    fun loadReward(
        adsKey: String,
        remoteKey: String,
    ) {
        RewardManager.loadRewardAds(this, adsKey, remoteKey)
    }

    fun showReward(
        adsKey: String,
        remoteKey: String,
        isReloadAfterShow: Boolean,
        onNextAction: () -> Unit
    ) {
        var earnedReward = false
        RewardManager.showRewardAds(this, adsKey, remoteKey, object : RewardedCallback() {
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
        adsKey: String,
        remoteKey: String,
        isReloadAfterShow: Boolean = true,
        onNextAction: () -> Unit
    ) {
        var earnedReward = false
        RewardManager.showRewardAds(
            this, adsKey, remoteKey, object : RewardedCallback() {
                override fun onUserEarnedReward() {
                    super.onUserEarnedReward()
                    earnedReward = true
                }

                override fun onNextAction() {
                    super.onNextAction()
                    if (earnedReward) {
                        showReward(adsKey, remoteKey, isReloadAfterShow) {
                            onNextAction.invoke()
                        }
                    }
                }
            },
            isReloadAfterShow
        )
    }*/

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkReceiver)
        SplashActivity.Companion.installStateUpdatedListener?.let { SplashActivity.Companion.appUpdateManager?.unregisterListener(it) }
    }

    override fun onResume() {
        super.onResume()
        SplashActivity.Companion.installStateUpdatedListener?.let { SplashActivity.Companion.appUpdateManager?.registerListener(it) }
        SplashActivity.Companion.appUpdateManager?.appUpdateInfo?.addOnSuccessListener { appUpdateInfo ->

            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                SplashActivity.Companion.appUpdateManager?.completeUpdate()
            }
        }
        lifecycleScope.launch {
            delay(250)
            if (Admob.getInstance().checkCondition(this@BaseActivity, Constants.RemoteKeys.resume_wb) &&
                this@BaseActivity !is SplashActivity &&
                this@BaseActivity !is WelcomeBackActivity
            ) {
                AppOpenManager.getInstance().enableAppResumeWithActivity(javaClass)
            }
        }
    }

    fun showActivity(activity: Class<*>, bundle: Bundle?) {
        val intent = Intent(this, activity)
        intent.putExtras(bundle ?: Bundle())
        startActivity(intent)
    }

    fun showActivityCustom(activity: Class<*>, bundle: Bundle?) {
        val intent = Intent(this, activity)
        intent.putExtras(bundle ?: Bundle())
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }


    private fun hideNavigationBar() {
        window.hideNavigation()
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this@BaseActivity.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    private fun baseView() {
        val icBack = binding.root.findViewById<View>(R.id.icBack)
        icBack?.tap { finish() }
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressCustom()
                icBack?.performClick()
            }
        }
        onBackPressedDispatcher.addCallback(this@BaseActivity, onBackPressedCallback)
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let {
            SystemUtil.setLocale(it)
        })
    }

}