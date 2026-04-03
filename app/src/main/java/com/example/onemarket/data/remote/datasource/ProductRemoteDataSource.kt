package com.example.onemarket.data.remote.datasource

import com.example.onemarket.data.remote.api.ProductApi
import com.example.onemarket.data.remote.dto.ProductDto
import javax.inject.Inject

class ProductRemoteDataSource @Inject constructor(
    private val api: ProductApi
) {
    suspend fun getProducts(): List<ProductDto> {
        return try {
            api.getProducts().products
                ?.filterNotNull()
                ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getProductById(id: Int): ProductDto? {
        return try {
            api.getProductById(id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getProductsByCategory(slug: String): List<ProductDto> {
        return try {
            api.getProductsByCategory(slug).products
                ?.filterNotNull()
                ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun searchProducts(query: String): List<ProductDto> {
        return try {
            api.searchProducts(query).products
                ?.filterNotNull()
                ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}