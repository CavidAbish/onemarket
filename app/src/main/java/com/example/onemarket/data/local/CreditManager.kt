package com.example.onemarket.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

data class CreditApplication(
    val id: Int,
    val productNames: String,
    val totalAmount: Double,
    val monthlyPayment: Double,
    val months: Int,
    val date: String,
    val status: String = "Gözləmədədir"
)

@Singleton
class CreditManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userManager: UserManager
) {
    private val gson = Gson()

    private fun prefs(): SharedPreferences =
        context.getSharedPreferences("${userManager.getUserKey()}_credit", Context.MODE_PRIVATE)

    fun getApplications(): List<CreditApplication> {
        val json = prefs().getString("applications", null) ?: return emptyList()
        val type = object : TypeToken<List<CreditApplication>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addApplication(app: CreditApplication) {
        val list = getApplications().toMutableList()
        list.add(0, app)
        prefs().edit().putString("applications", gson.toJson(list)).apply()
    }

    fun clearAll() {
        prefs().edit().remove("applications").apply()
    }
}
