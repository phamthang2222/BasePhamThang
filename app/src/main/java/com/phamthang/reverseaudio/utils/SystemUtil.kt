package com.phamthang.reverseaudio.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import java.util.Locale

object SystemUtil {
    private var myLocale: Locale? = null

    fun setLocale(context: Context): Context {
        val language = getPreLanguage(context)
        val langToApply = if (language.isBlank()) Locale.getDefault().toString() else language
        return changeLang(langToApply, context)
    }

    fun setLocaleSplash(context: Context): Context {
        val language = getPreLanguageSplash()
        val langToApply = if (language.isBlank()) Locale.getDefault().toString() else language
        return changeLang(langToApply, context)
    }



    private fun changeLang(languageCode: String, context: Context): Context {
        if (languageCode.isBlank()) return context

        val deviceLanguageParts = when {
            languageCode.contains("_") -> languageCode.split("_")
            languageCode.contains("-") -> languageCode.split("-")
            else -> listOf(languageCode)
        }
        val appLanguageCode = if (deviceLanguageParts.size > 1) {
            Locale(deviceLanguageParts[0], deviceLanguageParts[1])
        }else{
            Locale(deviceLanguageParts[0])
        }

        Locale.setDefault(appLanguageCode)
        val config = Configuration()
        config.setLocale(appLanguageCode)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            context
        }
    }

    fun saveLocale(context: Context, lang: String?) {
        setPreLanguage(context, lang)
    }

    fun getPreLanguage(mContext: Context): String {
        val preferences = mContext.getSharedPreferences("MY_PRE", Context.MODE_PRIVATE)
        Locale.getDefault().displayLanguage
        val lang = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Resources.getSystem().configuration.locales[0].language
        } else {
            Resources.getSystem().configuration.locale.language
        }
        if (!languageApp.contains(lang)) {
            return preferences.getString("KEY_LANGUAGE", "en")!!
        } else {
            return preferences.getString("KEY_LANGUAGE", lang)!!
        }
    }

    fun getPreLanguageSplash(): String {
        val defaultLang = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Resources.getSystem().configuration.locales[0].language
        } else {
            Resources.getSystem().configuration.locale.language
        }

        return if (!languageApp.contains(defaultLang)) "en" else defaultLang
    }


    fun setPreLanguage(context: Context, language: String?) {
        if (language == null || language == "") {
            return
        } else {
            val preferences = context.getSharedPreferences("MY_PRE", Context.MODE_PRIVATE)
            preferences.edit().putString("KEY_LANGUAGE", language).apply()
        }
    }

    private val languageApp: List<String>
        get() {
            val languages: MutableList<String> = ArrayList()
            languages.add("en")
            languages.add("hi")
            languages.add("es")
            languages.add("fr")
            languages.add("de")
            languages.add("in")
            languages.add("ar")
            languages.add("bn")
            languages.add("ja")
            languages.add("ko")
            languages.add("ru")
            languages.add("tr")
            languages.add("pt-rBR")
            languages.add("pt-rPT")
            languages.add("zh-rCN")
            languages.add("zh-rTW")
            languages.add("zh")
            languages.add("pt")

            return languages
        }

}