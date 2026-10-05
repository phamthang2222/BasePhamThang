package com.phamthang.base.ui.default_ui.setting

import android.view.View
import androidx.lifecycle.lifecycleScope
import com.vifarmer.ads.lib.Utils.EventTrackingHelper
import com.vifarmer.ads.lib.ads.app_open_ads.AppOpenManager
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.phamthang.base.R
import com.phamthang.base.base.BaseActivity
import com.phamthang.base.base.CommonVM
import com.phamthang.base.databinding.ActivitySettingBinding
import com.phamthang.base.extention.clickWithBounce
import com.phamthang.base.extention.getAppVersionName
import com.phamthang.base.rate.showRate
import com.phamthang.base.ui.default_ui.language.LanguageSettingActivity
import com.phamthang.base.utils.Constants
import com.phamthang.base.utils.DataProject
import com.phamthang.base.utils.SystemUtil
import com.phamthang.base.utils.policy
import com.phamthang.base.utils.shareApp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingActivity : BaseActivity<CommonVM, ActivitySettingBinding>(){

    override fun createViewModel(): Class<CommonVM> = CommonVM::class.java

    override fun getViewDataBinding(): ActivitySettingBinding = ActivitySettingBinding.inflate(layoutInflater)

    override fun initView() {
        EventTrackingHelper.logEvent(this, Constants.TrackingKeys.setting_view)
        if (sharePrefApp.isRated) {
            binding.llRate.visibility = View.GONE
        } else {
            binding.llRate.visibility = View.VISIBLE
        }
        val ver = getAppVersionName()
        binding.tvAppVersion.text = getString(R.string.version, ver)

        binding.llLanguage.clickWithBounce {
            EventTrackingHelper.logEvent(this, Constants.TrackingKeys.setting_language_click)
            showActivity(LanguageSettingActivity::class.java, null)
        }
        binding.llShare.clickWithBounce {
            EventTrackingHelper.logEvent(this, Constants.TrackingKeys.setting_share_click)
            shareApp()
        }

        binding.llPolicy.clickWithBounce {
            EventTrackingHelper.logEvent(this, Constants.TrackingKeys.setting_policy_click)
            policy()
        }
        binding.llRate.clickWithBounce {
            showRate(
                sharePrefApp = sharePrefApp,
                onSuccess = {
                    binding.llRate.visibility = View.GONE
                },
                onDismiss = {

                })
        }

        binding.tvCurrentLanguage.text = getCurrentLanguage()

    }

    override fun bindViewModel() {

    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            delay(500L)
            AppOpenManager.getInstance().enableAppResumeWithActivity(SettingActivity::class.java)
        }
    }


    private fun getCurrentLanguage(): String {
        val currentIsoLang = SystemUtil.getPreLanguage(this)
        val currentLang = DataProject.listLanguage().find {
            it.isoLanguage == currentIsoLang
        }?.languageName

        return currentLang ?: "UnKnown"
    }

}