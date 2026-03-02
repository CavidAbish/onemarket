package com.example.onemarket.domain.model

data class ProductModel(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val thumbnail: String,
    val rating: Double,
    val stock: Int,
    val brand: String,
    val category: String
)