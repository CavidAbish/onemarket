package com.example.onemarket.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

data class AppNotification(
    val id: Int,
    val orderId: Int,
    val title: String,
    val body: String,
    val type: String,
    val dateTime: String,
    val isRead: Boolean = false,
    val creditApplicationId: Int = 0
)

@Singleton
class AppNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userManager: UserManager
) {
    private val gson = Gson()

    private fun prefs(): SharedPreferences {
        val key = "${userManager.getUserKey()}_notifications"
        return context.getSharedPreferences(key, Context.MODE_PRIVATE)
    }

    fun getNotifications(): List<AppNotification> {
        val json = prefs().getString("notifications", null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<AppNotification>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addNotification(notification: AppNotification) {
        val list = getNotifications().toMutableList()
        list.add(0, notification)
        prefs().edit().putString("notifications", gson.toJson(list)).apply()
    }

    fun getUnreadCount(): Int = getNotifications().count { !it.isRead }

    fun markAllRead() {
        val updated = getNotifications().map { it.copy(isRead = true) }
        prefs().edit().putString("notifications", gson.toJson(updated)).apply()
    }

    fun clearAllNotifications() {
        prefs().edit().remove("notifications").apply()
    }
}
