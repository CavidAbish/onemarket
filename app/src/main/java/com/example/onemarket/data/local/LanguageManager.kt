package com.example.onemarket.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    fun getLanguage(): String = prefs.getString("language", "az") ?: "az"

    fun saveLanguage(lang: String) {
        prefs.edit().putString("language", lang).apply()
    }

    fun getLanguageLabel(): String = if (getLanguage() == "en") "English" else "Azərbaycan"

    companion object {
        fun readLanguage(context: Context): String =
            context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
                .getString("language", "az") ?: "az"
    }
}
