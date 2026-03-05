package com.example.onemarket.data.remote.datasource

import com.example.onemarket.data.remote.api.CategoryApi
import com.example.onemarket.data.remote.dto.CategoryDto
import javax.inject.Inject

class CategoryRemoteDataSource @Inject constructor(
    private val api: CategoryApi
) {
    suspend fun getCategories(): List<Pair<CategoryDto, String>> {
        return try {
            val categories = api.getCategories()
            categories.map { category ->
                val imageUrl = try {
                    api.getFirstProductByCategory(category.slug ?: "")
                        .products
                        ?.firstOrNull()
                        ?.thumbnail ?: ""
                } catch (e: Exception) {
                    ""
                }
                Pair(category, imageUrl)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}