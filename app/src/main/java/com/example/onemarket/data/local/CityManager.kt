package com.example.onemarket.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CityManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("city_prefs", Context.MODE_PRIVATE)

    fun getCity(): String = prefs.getString("selected_city", "Bakı") ?: "Bakı"

    fun saveCity(city: String) {
        prefs.edit().putString("selected_city", city).apply()
    }
}