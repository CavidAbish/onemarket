package com.example.onemarket.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.onemarket.domain.model.ProductModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecentlyViewedManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userManager: UserManager
) {
    private val gson = Gson()
    private val maxItems = 20

    private fun prefs(): SharedPreferences {
        val key = "${userManager.getUserKey()}_recently_viewed"
        return context.getSharedPreferences(key, Context.MODE_PRIVATE)
    }

    fun getRecentlyViewed(): List<ProductModel> {
        val json = prefs().getString("recently_viewed", null) ?: return emptyList()
        val type = object : TypeToken<List<ProductModel>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addProduct(product: ProductModel) {
        val list = getRecentlyViewed().toMutableList()
        list.removeAll { it.id == product.id }
        list.add(0, product)
        val trimmed = if (list.size > maxItems) list.take(maxItems) else list
        prefs().edit().putString("recently_viewed", gson.toJson(trimmed)).apply()
    }

    fun clearAll() {
        prefs().edit().remove("recently_viewed").apply()
    }
}