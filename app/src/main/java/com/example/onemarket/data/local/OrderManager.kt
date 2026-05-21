package com.example.onemarket.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.onemarket.domain.model.ProductModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

data class Order(
    val id: Int,
    val product: ProductModel,
    val quantity: Int,
    val totalAmount: Double,
    val date: String
)

@Singleton
class OrderManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userManager: UserManager
) {
    private val gson = Gson()

    private fun prefs(): SharedPreferences {
        val key = "${userManager.getUserKey()}_orders"
        return context.getSharedPreferences(key, Context.MODE_PRIVATE)
    }

    fun getOrders(): List<Order> {
        val json = prefs().getString("orders", null) ?: return emptyList()
        val type = object : TypeToken<List<Order>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addOrder(order: Order) {
        val orders = getOrders().toMutableList()
        orders.add(0, order)
        prefs().edit().putString("orders", gson.toJson(orders)).apply()
    }

    fun addOrdersFromCart(cartItems: List<CartManager.CartItem>) {
        val orders = getOrders().toMutableList()
        val date = java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault())
            .format(java.util.Date())
        cartItems.forEachIndexed { index, item ->
            orders.add(
                0, Order(
                    id = orders.size + index + 1,
                    product = item.product,
                    quantity = item.quantity,
                    totalAmount = item.product.price * item.quantity,
                    date = date
                )
            )
        }
        prefs().edit().putString("orders", gson.toJson(orders)).apply()
    }
}