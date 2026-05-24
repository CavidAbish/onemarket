package com.example.onemarket.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CityManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userManager: UserManager
) {
    private fun prefs(): SharedPreferences =
        context.getSharedPreferences("${userManager.getUserKey()}_city", Context.MODE_PRIVATE)

    fun getCity(): String = prefs().getString("selected_city", "Bakı") ?: "Bakı"

    fun saveCity(city: String) {
        prefs().edit().putString("selected_city", city).apply()
    }

    /** Seçilmiş şəhərin mərkəz koordinatları */
    fun getCoordinates(): Pair<Double, Double> = when (getCity()) {
        "Gəncə"      -> Pair(40.6828, 46.3606)
        "Sumqayıt"   -> Pair(40.5897, 49.6686)
        "Mingəçevir" -> Pair(40.7703, 47.0512)
        "Naxçıvan"   -> Pair(39.2092, 45.4107)
        "Lənkəran"   -> Pair(38.7529, 48.8476)
        "Şirvan"     -> Pair(39.9302, 48.9202)
        "Şəki"       -> Pair(41.1915, 47.1706)
        "Abşeron"    -> Pair(40.5060, 49.8710)
        else         -> Pair(40.4093, 49.8671)   // Bakı default
    }
}
