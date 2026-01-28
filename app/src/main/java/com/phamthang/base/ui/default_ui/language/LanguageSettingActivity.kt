package com.phamthang.base.ui.default_ui.language

import android.content.SharedPreferences
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import com.phamthang.base.ads.AdsHelper
import com.phamthang.base.base.BaseActivity
import com.phamthang.base.extention.logDebug
import com.phamthang.base.ui.feature.home.HomeActivity
import com.phamthang.base.utils.DataProject
import com.phamthang.base.utils.SystemUtil
import com.phamthang.base.databinding.ActivityLanguageSettingBinding
import com.phamthang.base.ui.default_ui.language.adapter.LanguageAdapter
import com.phamthang.base.ui.default_ui.language.model.LanguageModel
import com.phamthang.base.ui.default_ui.language.model.LanguageSubModel
import com.phamthang.base.utils.Constants


class LanguageSettingActivity : BaseActivity<LanguageViewModel, ActivityLanguageSettingBinding>() {

    private var adapter: LanguageAdapter? = null
    private var model : Any? = null
    private var sharedPreferences: SharedPreferences? = null
    private var currentLanguage : LanguageModel? = null
    private var currentApiVersion = 0

    override fun createViewModel(): Class<LanguageViewModel> = LanguageViewModel::class.java

    override fun getViewDataBinding(): ActivityLanguageSettingBinding = ActivityLanguageSettingBinding.inflate(layoutInflater)

    override fun initView() {
        sharedPreferences = getSharedPreferences("MY_PRE", MODE_PRIVATE)
        setUpListLang()
        binding.rclLanguage.adapter = adapter

        AdsHelper.loadBannerAds(this,this,binding.bannerContainerView, Constants.AdsKey.banner_setting)
    }

    private fun setUpListLang() {
        adapter = LanguageAdapter(
            setLanguageDefault(),
            onClickSubLanguage = {
                binding.ivDone.alpha = 1f
                binding.ivDone.isEnabled = true
                model = it
            },
            onClickDropDown = {
                logDebug(message = "ChekClickLFO: onClickDropDown")
            },
            onClickSingleLanguage = {
                logDebug(message = "ChekClickLFO: onClickSingleLanguage: ${it.languageName}-${it.isoLanguage}")
                model = it
            }
        )
        binding.rclLanguage.adapter = adapter
    }

    override fun bindViewModel() {

    }

    override fun onBackPressCustom() {
        finish()
    }


    private fun setLanguageDefault(): List<LanguageModel> {
        val lists: MutableList<LanguageModel> = DataProject.listLanguage()
        var data: LanguageModel? = null
        lists.forEach { languageModel ->
            if(languageModel.listLanguageSubModel.isNotEmpty()){
                languageModel.listLanguageSubModel.forEach { languageSubModel ->
                    if (languageSubModel.languageName == SystemUtil.getPreLanguageName(this)) {
                        this.model = languageSubModel
                        data = languageModel
                        languageSubModel.isCheck = true
                    }
                }
            }else{
                if(languageModel.languageName == SystemUtil.getPreLanguageName(this)){
                    this.model = languageModel
                    data = languageModel
                    languageModel.isCheck = true
                }
            }
        }
        data?.let {
            lists.remove(it)
            lists.add(0, it)
        }
        return lists
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    fun ivDone(v: View) {
        getSharedPreferences("MY_PRE", MODE_PRIVATE).edit() { putBoolean("openLanguage", true) }
        if (model is LanguageSubModel) {
            SystemUtil.setLanguage(this, (model as LanguageSubModel).isoLanguage, (model as LanguageSubModel).languageName)
        } else {
            SystemUtil.setLanguage(this, (model as LanguageModel).isoLanguage, (model as LanguageModel).languageName)
        }
        SystemUtil.setLocale(this)
        startNextAct()
    }

    private fun startNextAct() {
        showActivity(HomeActivity::class.java, null)
        finishAffinity()
    }
}