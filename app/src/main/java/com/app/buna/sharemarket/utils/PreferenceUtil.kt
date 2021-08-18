package com.app.buna.sharemarket.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

object PreferenceUtil {

    private fun getPreference(context: Context): SharedPreferences {
        return context.getSharedPreferences("pref", MODE_PRIVATE)
    }

    fun getString(context: Context, key: String, defValue: String): String {
        return getPreference(context).getString(key, defValue).toString()
    }

    fun putString(context: Context, key: String, value: String) {
        getPreference(context).edit().putString(key, value).apply()
    }

    fun getInt(context: Context, key: String, defValue: Int): Int {
        return getPreference(context).getInt(key, defValue)
    }

    fun putInt(context: Context, key: String, value: Int) {
        getPreference(context).edit().putInt(key, value).apply()
    }

    fun clearAllValue(context: Context) {
        val editor = getPreference(context).edit()
        editor.clear()
        editor.commit()
    }
}
