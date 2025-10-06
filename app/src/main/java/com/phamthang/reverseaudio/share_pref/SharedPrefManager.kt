package com.phamthang.reverseaudio.share_pref

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit


object SharedPrefManager {

    private const val PREF_NAME = "DATA_APP"
    const val FIRST_OPEN_APP = "FIRST_OPEN_APP"
    const val SELECTED_LANGUAGE = "SELECTED_LANGUAGE"

    private const val PRIVATE_MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
    }


    fun getIsFirstLaunch(key: String, defaultValue: Boolean = true): Boolean {
        return preferences.getBoolean(key, defaultValue)
    }
    // Save a String value
    fun saveString(key: String, value: String) {
        preferences.edit { putString(key, value) }
    }

    // Get a String value
    fun getString(key: String, defaultValue: String = ""): String {
        return preferences.getString(key, defaultValue) ?: defaultValue
    }

    // Save a Boolean value
    fun saveBoolean(key: String, value: Boolean) {
        preferences.edit{ putBoolean(key, value) }
    }

    // Get a Boolean value
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return preferences.getBoolean(key, defaultValue)
    }

    // Save an Int value
    fun saveInt(key: String, value: Int) {
        preferences.edit { putInt(key, value) }
    }

    // Get an Int value
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return preferences.getInt(key, defaultValue)
    }

    // Save a Float value
    fun saveFloat(key: String, value: Float) {
        preferences.edit{ putFloat(key, value) }
    }

    // Get a Float value
    fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return preferences.getFloat(key, defaultValue)
    }

    // Save a Long value
    fun saveLong(key: String, value: Long) {
        preferences.edit { putLong(key, value) }
    }

    // Get a Long value
    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return preferences.getLong(key, defaultValue)
    }

    // Remove a specific key-value pair
    fun remove(key: String) {
        preferences.edit{ remove(key) }
    }

    const val MAX_RECENT_SEARCHES = 3
    /*    fun saveRecentSearch(newKeyword: String, keyPref: String ) {
            val json = preferences.getString(keyPref, "[]")
            val type = object : TypeToken<MutableList<String>>() {}.type
            val list: MutableList<String> = Gson().fromJson(json, type) ?: mutableListOf()

            list.remove(newKeyword)
            list.add(0, newKeyword)
            if (list.size > MAX_RECENT_SEARCHES) {
                list.removeLast()
            }

            val newJson = Gson().toJson(list)
            preferences.edit {
                putString(keyPref, newJson)
            }
        }

        fun getRecentSearches(keyPref: String): List<String> {
            val json = preferences.getString(keyPref, "[]")
            val type = object : TypeToken<List<String>>() {}.type
            return Gson().fromJson(json, type) ?: emptyList()
        }

        fun removeRecentSearch(keyword: String, keyPref:String) {
            val json = preferences.getString(keyPref, "[]")
            val type = object : TypeToken<MutableList<String>>() {}.type
            val list: MutableList<String> = Gson().fromJson(json, type) ?: mutableListOf()

            if (list.contains(keyword)) {
                list.remove(keyword)
                val newJson = Gson().toJson(list)
                preferences.edit {
                    putString(keyPref, newJson)
                }
            }
        }*/


    fun clear() {
        preferences.edit{ clear() }
    }

    fun increaseCountFunction(key: String, defaultValue: Int = 1) {
        val value = preferences.getInt(key, defaultValue)
        preferences.edit{ putInt(key, value + 1) }
    }

}