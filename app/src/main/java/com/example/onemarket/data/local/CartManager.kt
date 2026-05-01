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
class CartManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userManager: UserManager
) {
    private val gson = Gson()

    data class CartItem(val product: ProductModel, val quantity: Int)

    private fun prefs(): SharedPreferences {
        val key = "${userManager.getUserKey()}_cart"
        return context.getSharedPreferences(key, Context.MODE_PRIVATE)
    }

    fun getCartItems(): List<CartItem> {
        val json = prefs().getString("cart_items", null) ?: return emptyList()
        val type = object : TypeToken<List<CartItem>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addToCart(product: ProductModel) {
        val items = getCartItems().toMutableList()
        val existing = items.indexOfFirst { it.product.id == product.id }
        if (existing >= 0) items[existing] = items[existing].copy(quantity = items[existing].quantity + 1)
        else items.add(CartItem(product, 1))
        save(items)
    }

    fun removeFromCart(productId: Int) {
        save(getCartItems().filter { it.product.id != productId })
    }

    fun updateQuantity(productId: Int, quantity: Int) {
        val items = getCartItems().toMutableList()
        val idx = items.indexOfFirst { it.product.id == productId }
        if (idx >= 0) {
            if (quantity <= 0) items.removeAt(idx)
            else items[idx] = items[idx].copy(quantity = quantity)
        }
        save(items)
    }

    fun getTotalPrice(): Double = getCartItems().sumOf { it.product.price * it.quantity }
    fun getItemCount(): Int = getCartItems().sumOf { it.quantity }
    fun isInCart(productId: Int): Boolean = getCartItems().any { it.product.id == productId }

    fun clearCart() {
        prefs().edit().remove("cart_items").apply()
    }

    private fun save(items: List<CartItem>) {
        prefs().edit().putString("cart_items", gson.toJson(items)).apply()
    }
}