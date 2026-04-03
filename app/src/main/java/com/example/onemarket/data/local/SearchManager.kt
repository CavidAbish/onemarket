package com.example.onemarket.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val maxHistory = 10

    fun getHistory(): List<String> {
        val json = prefs.getString("search_history", null) ?: return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addToHistory(query: String) {
        if (query.isBlank()) return
        val history = getHistory().toMutableList()
        history.removeAll { it.equals(query, ignoreCase = true) }
        history.add(0, query)
        val trimmed = if (history.size > maxHistory) history.take(maxHistory) else history
        prefs.edit().putString("search_history", gson.toJson(trimmed)).apply()
    }

    fun clearHistory() {
        prefs.edit().remove("search_history").apply()
    }
}