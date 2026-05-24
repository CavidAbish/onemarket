package com.example.onemarket.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PickupHistoryManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userManager: UserManager
) {
    private val gson = Gson()

    private fun prefs(): SharedPreferences =
        context.getSharedPreferences("${userManager.getUserKey()}_pickup", Context.MODE_PRIVATE)

    // ---- Pickup məntəqə tarixçəsi ----
    fun getPickupHistory(): List<PickupPoint> {
        val json = prefs().getString("pickup_history", null) ?: return emptyList()
        val type = object : TypeToken<List<PickupPoint>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addPickupPoint(point: PickupPoint) {
        val history = getPickupHistory().toMutableList()
        history.removeAll { it.id == point.id }   // duplikat olmasın
        history.add(0, point)
        val limited = history.take(5)             // son 5 məntəqə
        prefs().edit().putString("pickup_history", gson.toJson(limited)).apply()
    }

    fun getLastPickup(): PickupPoint? = getPickupHistory().firstOrNull()

    // ---- Çatdırılma ünvan tarixçəsi ----
    fun getAddressHistory(): List<String> {
        val json = prefs().getString("address_history", null) ?: return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addAddress(address: String) {
        if (address.isBlank()) return
        val history = getAddressHistory().toMutableList()
        history.removeAll { it == address }
        history.add(0, address)
        val limited = history.take(5)
        prefs().edit().putString("address_history", gson.toJson(limited)).apply()
    }

    fun clearAll() {
        prefs().edit().clear().apply()
    }
}
