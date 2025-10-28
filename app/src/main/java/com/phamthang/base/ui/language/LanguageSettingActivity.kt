package com.phamthang.base.ui.language

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.phamthang.base.base.BaseActivity
import com.phamthang.base.extention.visible
import com.phamthang.base.ui.home.HomeActivity
import com.phamthang.base.utils.SystemUtil
import com.phamthang.base.R
import com.phamthang.base.databinding.ActivityLanguageSettingBinding
import java.util.Locale


class LanguageSettingActivity : BaseActivity<LanguageViewModel, ActivityLanguageSettingBinding>(), IClickLanguage {

    private var adapter: LanguageAdapter? = null
    private var model: LanguageModel = LanguageModel()
    private var sharedPreferences: SharedPreferences? = null
    private var currentLanguage : LanguageModel? = null

    private var currentApiVersion = 0

    override fun createViewModel(): Class<LanguageViewModel> = LanguageViewModel::class.java

    override fun getViewDataBinding(): ActivityLanguageSettingBinding = ActivityLanguageSettingBinding.inflate(layoutInflater)

    override fun initView() {

        sharedPreferences = getSharedPreferences("MY_PRE", MODE_PRIVATE)

        adapter = LanguageAdapter(
            this,
            setLanguageDefault(),
            this,
            false
        )

        binding.rclLanguage.adapter = adapter

    }

    override fun bindViewModel() {

    }

    override fun onBackPressCustom() {
        finish()
    }

    override fun onClick(data: LanguageModel) {
        model = data
        model = data
        val isArabic = model.isoLanguage == "ar"
        window.decorView.layoutDirection = if(isArabic) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR
        binding.tvLanguage.text = getLocalizedString(this, model.isoLanguage, R.string.language)

        binding.ivDone.visible()

    }

    private fun setLanguageDefault(): List<LanguageModel> {
        val lists: MutableList<LanguageModel> = ArrayList()
        val key: String = SystemUtil.getPreLanguage(this)
        val systemLang: String = Resources.getSystem().configuration.locales[0].language
        Log.d("CHECK_systemLang", "systemLang: ${systemLang} ")

        lists.add(LanguageModel("Español", "es", false, R.drawable.ic_span_flag))
        lists.add(LanguageModel("Français", "fr", false, R.drawable.ic_french_flag))
        lists.add(LanguageModel("हिन्दी", "hi", false, R.drawable.ic_hindi_flag))
        lists.add(LanguageModel("English", "en", false, R.drawable.ic_english_flag))
        lists.add(LanguageModel("Português (Brazil)", "pt-rBR", false, R.drawable.ic_brazil_flag))
        lists.add(LanguageModel("Português (Portu)", "pt-rPT", false, R.drawable.ic_portuguese_flag))
        lists.add(LanguageModel("日本語", "ja", false, R.drawable.ic_japan_flag))
        lists.add(LanguageModel("Deutsch", "de", false, R.drawable.ic_german_flag))
        lists.add(LanguageModel("中文 (简体)", "zh-rCN", false, R.drawable.ic_china_flag))
        lists.add(LanguageModel("中文 (繁體) ", "zh-rTW", false, R.drawable.ic_china_flag))
        lists.add(LanguageModel("عربي", "ar", false, R.drawable.ic_a_rap_flag))
        lists.add(LanguageModel("বাংলা ", "bn", false, R.drawable.ic_bengali_flag))
        lists.add(LanguageModel("Русский ", "ru", false, R.drawable.ic_russia_flag))
        lists.add(LanguageModel("Türkçe ", "tr", false, R.drawable.ic_turkey_flag))
        lists.add(LanguageModel("한국인 ", "ko", false, R.drawable.ic_korean_flag))
        lists.add(LanguageModel("Indonesia", "in", false, R.drawable.ic_indo_flag))

        for (model in lists) {
            if (model.isoLanguage == key) {
                this.model = model
            }
        }

        for (i in lists.indices) {
            if (!sharedPreferences!!.getBoolean("nativeLanguage", false)) {
                if (key == lists[i].isoLanguage) {
                    val data = lists[i]
                    currentLanguage = data
                    data.isCheck = true
                    lists.remove(lists[i])
                    lists.add(0, data)
                    break
                }
            } else {
                if (key == lists[i].isoLanguage) {
                    lists[i].isCheck = true
                }
            }
        }

        return lists
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    fun ivDone(v: View) {
        val editor: SharedPreferences.Editor =
            getSharedPreferences("MY_PRE", MODE_PRIVATE).edit()
        editor.putBoolean("openLanguage", true)
        editor.apply()
        SystemUtil.setPreLanguage(this@LanguageSettingActivity, model.isoLanguage)
        SystemUtil.setLocale(this)
        startNextAct()
    }

    private fun startNextAct() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
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
}