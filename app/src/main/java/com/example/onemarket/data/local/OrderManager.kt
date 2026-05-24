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
    val date: String,
    val paymentMethod: String = "Bank kartı vasitəsi ilə onlayn",
    val status: String = "paid",
    val orderDateTime: String = "",
    val deliveryAddress: String = "",
    val originalAmountSum: Double = 0.0,
    val discountSum: Double = 0.0,
    val deliveryCost: Double = 3.0
)

@Singleton
class OrderManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userManager: UserManager,
    private val notificationManager: AppNotificationManager
) {
    private val gson = Gson()

    private fun prefs(): SharedPreferences {
        val key = "${userManager.getUserKey()}_orders"
        return context.getSharedPreferences(key, Context.MODE_PRIVATE)
    }

    fun getOrders(): List<Order> {
        val json = prefs().getString("orders", null) ?: return emptyList()
        val type = object : TypeToken<List<Order>>() {}.type
        val raw: List<Order> = gson.fromJson(json, type)

        return raw.map { normalizeOrder(it) }
    }


    private fun normalizeOrder(o: Order): Order {
        val product: ProductModel? = o.product
        val date: String?           = o.date
        val paymentMethod: String?  = o.paymentMethod
        val status: String?         = o.status
        val orderDateTime: String?  = o.orderDateTime
        val deliveryAddress: String? = o.deliveryAddress


        if (product != null && date != null && paymentMethod != null &&
            status != null && orderDateTime != null && deliveryAddress != null) {
            return o
        }

        return Order(
            id               = o.id,
            product          = product?.let { normalizeProduct(it) }
                               ?: ProductModel(0, "", 0.0, "", "", 0.0, 0, "", ""),
            quantity         = o.quantity,
            totalAmount      = o.totalAmount,
            date             = date          ?: "",
            paymentMethod    = paymentMethod ?: "Bank kartı vasitəsi ilə onlayn",
            status           = status        ?: "paid",
            orderDateTime    = orderDateTime ?: "",
            deliveryAddress  = deliveryAddress ?: "",
            originalAmountSum = o.originalAmountSum,
            discountSum      = o.discountSum,
            deliveryCost     = o.deliveryCost
        )
    }


    private fun normalizeProduct(p: ProductModel): ProductModel {
        val title: String?       = p.title
        val description: String? = p.description
        val thumbnail: String?   = p.thumbnail
        val brand: String?       = p.brand
        val category: String?    = p.category

        if (title != null && description != null && thumbnail != null &&
            brand != null && category != null) {
            return p
        }

        return ProductModel(
            id                 = p.id,
            title              = title       ?: "",
            price              = p.price,
            description        = description ?: "",
            thumbnail          = thumbnail   ?: "",
            rating             = p.rating,
            stock              = p.stock,
            brand              = brand       ?: "",
            category           = category    ?: "",
            discountPercentage = p.discountPercentage,
            originalPrice      = p.originalPrice,
            monthlyPayment     = p.monthlyPayment
        )
    }

    fun addOrder(order: Order) {
        val orders = getOrders().toMutableList()
        orders.add(0, order)
        prefs().edit().putString("orders", gson.toJson(orders)).apply()
    }

    fun addOrdersFromCart(
        cartItems: List<CartManager.CartItem>,
        paymentMethod: String = "Bank kartı vasitəsi ilə onlayn",
        deliveryAddress: String = ""
    ) {
        val orders = getOrders().toMutableList()
        val now = java.util.Date()
        val dateFmt = java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault())
        val timeFmt = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        val date = dateFmt.format(now)
        val orderDateTime = "${timeFmt.format(now)} $date"


        val baseId = (System.currentTimeMillis() % 1_000_000_000L).toInt()

        cartItems.forEachIndexed { index, item ->
            val orderId = baseId + index
            val originalSum = item.product.originalPrice * item.quantity
            val discount = (item.product.originalPrice - item.product.price) * item.quantity
            orders.add(
                0, Order(
                    id = orderId,
                    product = item.product,
                    quantity = item.quantity,
                    totalAmount = item.product.price * item.quantity + 3.0,
                    date = date,
                    paymentMethod = paymentMethod,
                    status = "paid",
                    orderDateTime = orderDateTime,
                    deliveryAddress = deliveryAddress,
                    originalAmountSum = originalSum,
                    discountSum = discount,
                    deliveryCost = 3.0
                )
            )

            val (notifTitle, notifBody, notifType) = when {
                paymentMethod == "Təhvil alarkən bank kartı vasitəsi ilə" ->
                    Triple(
                        "Sifariş ödənişi gözləyir",
                        "Onlayn ödənişi tamamlayın və ya 1 saat ərzində başqa üsul seçin — əks halda $orderId nömrəli sifariş ləğv ediləcək",
                        "delivery_pending"
                    )
                paymentMethod.contains("Birbank", ignoreCase = true) ->
                    Triple(
                        "Sifariş ödənilib",
                        "$orderId nömrəli sifarişiniz Birbank taksit kartı ilə uğurla ödənildi",
                        "paid"
                    )
                else ->
                    Triple(
                        "Sifariş ödənilib",
                        "$orderId nömrəli sifarişiniz bank kartı vasitəsilə uğurla ödənildi",
                        "paid"
                    )
            }
            notificationManager.addNotification(
                AppNotification(
                    id = orderId,
                    orderId = orderId,
                    title = notifTitle,
                    body = notifBody,
                    type = notifType,
                    dateTime = orderDateTime
                )
            )
        }
        prefs().edit().putString("orders", gson.toJson(orders)).apply()
    }

    fun clearAllOrders() {
        prefs().edit().remove("orders").apply()
    }

    fun cancelOrder(orderId: Int) {
        val updated = getOrders().map { order ->
            if (order.id == orderId) order.copy(status = "cancelled") else order
        }
        prefs().edit().putString("orders", gson.toJson(updated)).apply()


        val now = java.util.Date()
        val timeFmt = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        val dateFmt = java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault())
        val dt = "${timeFmt.format(now)} ${dateFmt.format(now)}"


        val cancelledOrder = updated.find { it.id == orderId }
        val notifBody = if (cancelledOrder?.paymentMethod == "Təhvil alarkən bank kartı vasitəsi ilə") {
            "$orderId nömrəli sifariş ləğv edildi"
        } else {
            "$orderId nömrəli sifariş ləğv edildi. Vəsait 5 iş günü ərzində kartınıza qaytarılacaq"
        }

        notificationManager.addNotification(
            AppNotification(
                id = (System.currentTimeMillis() % 1_000_000_000L).toInt(),
                orderId = orderId,
                title = "Sifariş ləğv edildi",
                body = notifBody,
                type = "cancelled",
                dateTime = dt
            )
        )
    }
}