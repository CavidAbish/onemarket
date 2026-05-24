package com.example.onemarket.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Hər istifadəçi üçün ayrıca dil seçimini saxlayır.
 * Dəstəklənən dillər: "az" (Azərbaycan), "ru" (Русский), "en" (English), "tr" (Türkçe)
 */
@Singleton
class LanguageManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userManager: UserManager
) {
    private fun prefs(): SharedPreferences =
        context.getSharedPreferences("${userManager.getUserKey()}_language", Context.MODE_PRIVATE)

    fun getLanguage(): String = prefs().getString("language", "az") ?: "az"

    fun saveLanguage(lang: String) {
        prefs().edit().putString("language", lang).apply()
    }

    fun getLanguageLabel(): String = when (getLanguage()) {
        "ru" -> "Русский"
        "en" -> "English"
        "tr" -> "Türkçe"
        else -> "Azərbaycan"
    }
}
