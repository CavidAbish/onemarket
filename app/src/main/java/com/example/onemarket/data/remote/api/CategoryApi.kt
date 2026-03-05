package com.example.onemarket.data.remote.api

import com.example.onemarket.data.remote.dto.CategoryDto
import retrofit2.http.GET

interface CategoryApi {

    @GET("products/categories")
    suspend fun getCategories(): List<CategoryDto>
}