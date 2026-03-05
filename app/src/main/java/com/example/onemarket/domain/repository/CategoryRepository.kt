package com.example.onemarket.domain.repository

import com.example.onemarket.domain.model.CategoryModel

interface CategoryRepository {
    suspend fun getCategories(): List<CategoryModel>
}