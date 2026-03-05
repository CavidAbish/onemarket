package com.example.onemarket.data.remote.datasource

import com.example.onemarket.data.remote.api.CategoryApi
import com.example.onemarket.data.remote.dto.CategoryDto
import javax.inject.Inject

class CategoryRemoteDataSource @Inject constructor(
    private val api: CategoryApi
) {
    suspend fun getCategories(): List<CategoryDto> {
        return try {
            api.getCategories()
        } catch (e: Exception) {
            emptyList()
        }
    }
}


