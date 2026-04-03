package com.example.onemarket.data.remote.api

import com.example.onemarket.data.remote.dto.ProductDto
import com.example.onemarket.data.remote.dto.ProductResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {
    @GET("products")
    suspend fun getProducts(): ProductResponseDto

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): ProductDto

    @GET("products/search")
    suspend fun searchProducts(@Query("q") query: String): ProductResponseDto

    @GET("products/category/{slug}")
    suspend fun getProductsByCategory(@Path("slug") slug: String): ProductResponseDto
}