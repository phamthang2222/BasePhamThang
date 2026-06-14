package com.phamthang.base.base

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.amazic.library.ads.admob.Admob
import com.amazic.library.ads.app_open_ads.AppOpenManager
import com.google.android.play.core.install.model.InstallStatus
import com.phamthang.base.custome_view.LoadingView
import com.phamthang.base.extention.hideNavigation
import com.phamthang.base.extention.tap

import com.phamthang.base.utils.Constants
import com.phamthang.base.utils.SystemUtil
import com.phamthang.base.R
import com.phamthang.base.share_pref.SharePrefApp
import com.phamthang.base.ui.default_ui.no_internet.NetworkReceiver
import com.phamthang.base.ui.default_ui.no_internet.NoInternetActivity
import com.phamthang.base.ui.default_ui.splash.SplashActivity
import com.phamthang.base.ui.default_ui.welcome_back.WelcomeBackActivity
import com.phamthang.base.ui.feature.home.HomeActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseActivity<VM : ViewModel, T : ViewBinding> : AppCompatActivity() {

    abstract fun createViewModel(): Class<VM>
    abstract fun getViewDataBinding(): T
    abstract fun initView()
    abstract fun bindViewModel()

    open fun onBackPressCustom() {
        onBackPressedCallback.isEnabled = false
        onBackPressedDispatcher.onBackPressed()
        onBackPressedCallback.isEnabled = true
    }

    open fun shouldAllowBackWithoutId(): Boolean {
        return false
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val icBack = findViewById<View>(R.id.icBack)
            if (icBack != null || shouldAllowBackWithoutId()) {
                onBackPressCustom()
            }
        }
    }
    lateinit var viewModel: VM
    lateinit var binding: T

    private val TAG = "BaseActivity"
    private var currentApiVersion = 0
    private var networkReceiver: NetworkReceiver? = null

    private lateinit var loadingDialog: Dialog

    @Inject
    lateinit var sharePrefApp: SharePrefApp

    override fun onCreate(savedInstanceState: Bundle?) {
        SystemUtil.setLocale(this)
        hideNavigationBar()

        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[createViewModel()]
        setupNetworkReceiver()
        initView()
        bindViewModel()
        baseView()

    }

    private fun setupNetworkReceiver() {
        networkReceiver = NetworkReceiver { isConnected ->
            onNetworkChanged(isConnected)
        }
        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(networkReceiver, intentFilter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(networkReceiver, intentFilter)
        }
    }

    open fun onNetworkChanged(isConnected: Boolean) {
        if (!isConnected) {
            showActivityCustom(NoInternetActivity::class.java, null)
        } else {
            if (this is NoInternetActivity) {
                val screen = intent?.extras?.getString(Constants.ScreenKey.SCREEN)
                if (screen == Constants.ScreenKey.SPLASH_ACTIVITY) {
                    val myIntent = Intent(this, SplashActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(myIntent)
                } else {
                    finish()
                }
            }
        }
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


    override fun onDestroy() {
        super.onDestroy()
        networkReceiver?.let {
            try {
                unregisterReceiver(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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
        enableEdgeToEdge()
        window.hideNavigation()
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this@BaseActivity.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    private fun baseView() {
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        val icBack = findViewById<View>(R.id.icBack)
        icBack?.setOnClickListener {
            onBackPressCustom()
        }
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