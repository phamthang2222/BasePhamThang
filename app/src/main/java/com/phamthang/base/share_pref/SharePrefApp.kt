package com.phamthang.base.share_pref

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharePrefApp @Inject constructor(
    @ApplicationContext context: Context
) {
    companion object {
        const val SHARED_PREF_NAME = "SharePrefConfigApp"
        const val FREE_THEME_VOICE_LOCK_IDS = "FREE_THEME_VOICE_LOCK_IDS"

        const val FIRST_OPEN_APP = "FIRST_OPEN_APP"
        const val RATING_APPEAR = "RatingAppear"

    }

    private val sharePref = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)


    var listThemeFree : Set<String>
        get() = sharePref.getStringSet(FREE_THEME_VOICE_LOCK_IDS, emptySet()) ?: emptySet()
        set(value) = sharePref.edit() { putStringSet(FREE_THEME_VOICE_LOCK_IDS, value) }

    var isFirstOpenApp : Boolean
        get() = sharePref.getBoolean(FIRST_OPEN_APP, true)
        set(value) = sharePref.edit() { putBoolean(FIRST_OPEN_APP, value) }

    var countRatingAppear : Int
        get() = sharePref.getInt(RATING_APPEAR, 1)
        set(value) = sharePref.edit {
            putInt(RATING_APPEAR, value)
        }

    fun increaseCountRatingAppear(){
        val value = sharePref.getInt(RATING_APPEAR, 1)
        sharePref.edit {
            putInt(RATING_APPEAR, value + 1)
        }
    }

}