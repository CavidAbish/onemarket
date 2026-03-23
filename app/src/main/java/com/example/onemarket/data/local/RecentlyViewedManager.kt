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
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("recently_viewed_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val maxItems = 20

    fun getRecentlyViewed(): List<ProductModel> {
        val json = prefs.getString("recently_viewed", null) ?: return emptyList()
        val type = object : TypeToken<List<ProductModel>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addProduct(product: ProductModel) {
        val list = getRecentlyViewed().toMutableList()
        // Əgər artıq varsa, sil — yenidən əvvələ əlavə et
        list.removeAll { it.id == product.id }
        list.add(0, product)
        // Maksimum 20 məhsul saxla
        val trimmed = if (list.size > maxItems) list.take(maxItems) else list
        prefs.edit().putString("recently_viewed", gson.toJson(trimmed)).apply()
    }
}