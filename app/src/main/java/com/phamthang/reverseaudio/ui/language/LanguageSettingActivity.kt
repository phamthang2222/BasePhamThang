package com.phamthang.reverseaudio.ui.language

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.phamthang.reverseaudio.R
import com.phamthang.reverseaudio.databinding.ActivityLanguageSettingBinding
import com.phamthang.reverseaudio.base.BaseActivity
import com.phamthang.reverseaudio.extention.visible
import com.phamthang.reverseaudio.ui.home.HomeActivity
import com.phamthang.reverseaudio.utils.SystemUtil
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

        lists.add(LanguageModel("Français", "fr", false, R.drawable.flag_fra))
        lists.add(LanguageModel("Español", "es", false, R.drawable.flag_span))
        lists.add(LanguageModel("हिन्दी", "hi", false, R.drawable.flag_hindi))
        lists.add(LanguageModel("English", "en", false, R.drawable.flag_en))
        lists.add(LanguageModel("Portuguese (Brazil)", "pt-rBR", false, R.drawable.flag_port_brazil))
        lists.add(LanguageModel("Portuguese (Portugal)", "pt-rPT", false, R.drawable.flag_port_portu))
        lists.add(LanguageModel("中文 (简体)", "zh-rCN", false, R.drawable.flag_china))
        lists.add(LanguageModel("中文 (繁體) ", "zh-rTW", false, R.drawable.flag_china))
        lists.add(LanguageModel("عربي", "ar", false, R.drawable.flag_a_rap))
        lists.add(LanguageModel("বাংলা ", "bn", false, R.drawable.flag_bengali))
        lists.add(LanguageModel("Русский ", "ru", false, R.drawable.flag_russia))
        lists.add(LanguageModel("Deutsch", "de", false, R.drawable.flag_ger))
        lists.add(LanguageModel("日本語", "ja", false, R.drawable.flag_japan))
        lists.add(LanguageModel("Türkçe ", "tr", false, R.drawable.flag_turkiye))
        lists.add(LanguageModel("한국인 ", "ko", false, R.drawable.flag_korean))
        lists.add(LanguageModel("Indonesian", "in", false, R.drawable.flag_indonesia))


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