package com.rid.videosapp.sharedPref

import android.content.Context
import android.content.SharedPreferences

object PrefUtils {
    private val PREF_NAME = "_Pref_"
    val PREF_KEY_VIDEO_LOCATION="video_path"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun setString(context: Context, key: String, value: String?) {
        if (value != null && !value.isEmpty())
            getPreferences(context).edit().putString(key, value).commit()
    }

    fun clearStringData(context: Context, key: String) {
        getPreferences(context).edit().putString(key, null).apply()
    }

    fun getString(context: Context, key: String): String? {
        return getPreferences(context).getString(key, null)
    }

    fun setLong(context: Context, key: String, value: Long) {
        getPreferences(context).edit().putLong(key, value).apply()
    }

    fun getLong(context: Context, key: String): Long {
        return getPreferences(context).getLong(key, 0)
    }

}
