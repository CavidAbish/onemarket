package com.example.onemarket.data.remote.dto

import com.example.onemarket.domain.model.ProductModel

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
        category = category ?: ""
    )
}