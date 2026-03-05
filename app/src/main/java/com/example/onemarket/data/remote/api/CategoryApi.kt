package com.example.onemarket.data.remote.api

import com.example.onemarket.data.remote.dto.CategoryDto
import com.example.onemarket.data.remote.dto.ProductResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CategoryApi {

    @GET("products/categories")
    suspend fun getCategories(): List<CategoryDto>

    @GET("products/category/{slug}")
    suspend fun getFirstProductByCategory(
        @Path("slug") slug: String,
        @Query("limit") limit: Int = 1
    ): ProductResponseDto
}