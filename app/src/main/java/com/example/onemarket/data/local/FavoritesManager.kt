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
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("favorites_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Bütün like-ları al
    fun getFavorites(): List<ProductModel> {
        val json = prefs.getString("favorites", null) ?: return emptyList()
        val type = object : TypeToken<List<ProductModel>>() {}.type
        return gson.fromJson(json, type)
    }

    // Like et / like-ı geri al
    fun toggleFavorite(product: ProductModel) {
        val favorites = getFavorites().toMutableList()
        val exists = favorites.any { it.id == product.id }
        if (exists) {
            favorites.removeAll { it.id == product.id }
        } else {
            favorites.add(product)
        }
        saveFavorites(favorites)
    }

    // Like olunubmu?
    fun isFavorite(productId: Int): Boolean {
        return getFavorites().any { it.id == productId }
    }

    // Saxla
    private fun saveFavorites(favorites: List<ProductModel>) {
        prefs.edit().putString("favorites", gson.toJson(favorites)).apply()
    }
}