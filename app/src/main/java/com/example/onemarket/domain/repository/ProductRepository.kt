package com.example.onemarket.domain.repository

import com.example.onemarket.domain.model.ProductModel

interface ProductRepository {

    suspend fun getProducts(): List<ProductModel>

    suspend fun getProductById(id: Int): ProductModel?
    suspend fun getProductsByCategory(slug: String): List<ProductModel>
}