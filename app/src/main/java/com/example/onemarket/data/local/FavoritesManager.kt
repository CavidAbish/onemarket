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
class FavoritesManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userManager: UserManager
) {
    private val gson = Gson()

    private fun prefs(): SharedPreferences {
        val key = "${userManager.getUserKey()}_favorites"
        return context.getSharedPreferences(key, Context.MODE_PRIVATE)
    }

    fun getFavorites(): List<ProductModel> {
        val json = prefs().getString("favorites", null) ?: return emptyList()
        val type = object : TypeToken<List<ProductModel>>() {}.type
        return gson.fromJson(json, type)
    }

    fun toggleFavorite(product: ProductModel) {
        val favorites = getFavorites().toMutableList()
        val exists = favorites.any { it.id == product.id }
        if (exists) favorites.removeAll { it.id == product.id }
        else favorites.add(product)
        prefs().edit().putString("favorites", gson.toJson(favorites)).apply()
    }

    fun isFavorite(productId: Int): Boolean =
        getFavorites().any { it.id == productId }

    fun clearFavorites() {
        prefs().edit().remove("favorites").apply()
    }
}