package com.example.onemarket.data.remote.dto

import com.example.onemarket.domain.model.ProductModel
import java.lang.Math

fun ProductDto.toProductModel(): ProductModel {
    return ProductModel(
        id = id ?: 0,
        title = title ?: "",
        price = price ?: 0.0,
        description = description ?: "",
        thumbnail = thumbnail ?: "",
        rating = rating ?: 0.0,
        stock = stock ?: 0,
        brand = brand ?: "",
        category = category ?: "",
        discountPercentage = discountPercentage ?: 0.0,
        originalPrice = calculateOriginalPrice(
            price ?: 0.0,
            discountPercentage ?: 0.0
        ),
        monthlyPayment =   Math.round(((price ?: 0.0) / 12) * 100.0) / 100.0
    )
}



private fun calculateOriginalPrice(price: Double, discount: Double): Double {
    return if (discount > 0) {
        val original=price / (1 - discount / 100)
        Math.round(original * 100.0) / 100.0
    } else {
        price
    }
}