package com.phamthang.reverseaudio.share_pref

import android.content.Context
import androidx.core.content.edit

class SharePrefApp(context: Context) {
    companion object {
        const val SHARED_PREF_NAME = "SharePrefConfigApp"
        const val FREE_THEME_VOICE_LOCK_IDS = "FREE_THEME_VOICE_LOCK_IDS"


        private var _instance: SharePrefApp? = null
        val instance: SharePrefApp get() = _instance!!
        fun init(context: Context) {
            if (_instance == null) _instance = SharePrefApp(context)
        }
    }

    private val sharePref = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)


    var listThemeFree : Set<String>
        get() = sharePref.getStringSet(FREE_THEME_VOICE_LOCK_IDS, emptySet()) ?: emptySet()
        set(value) = sharePref.edit() { putStringSet(FREE_THEME_VOICE_LOCK_IDS, value) }
}