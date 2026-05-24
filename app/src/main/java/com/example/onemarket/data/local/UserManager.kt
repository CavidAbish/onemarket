package com.example.onemarket.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveUser(name: String, phone: String) {
        prefs.edit()
            .putString("user_name", name)
            .putString("user_phone", phone)
            .putBoolean("is_logged_in", true)
            .apply()
    }

    fun getUserName(): String = prefs.getString("user_name", "") ?: ""
    fun getUserPhone(): String = prefs.getString("user_phone", "") ?: ""
    fun isLoggedIn(): Boolean = prefs.getBoolean("is_logged_in", false)


    fun getUserKey(): String {
        val phone = getUserPhone().replace("+", "").replace(" ", "")
        return if (phone.isNotEmpty()) "user_$phone" else "user_guest"
    }

    fun logout() {
        prefs.edit()
            .putBoolean("is_logged_in", false)
            .remove("user_name")
            .remove("user_phone")
            .apply()
    }
}