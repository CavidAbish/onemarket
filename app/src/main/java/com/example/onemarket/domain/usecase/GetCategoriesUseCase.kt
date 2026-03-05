package com.example.onemarket.domain.usecase

import com.example.onemarket.domain.model.CategoryModel
import com.example.onemarket.domain.repository.CategoryRepository
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(): List<CategoryModel> {
        return repository.getCategories()
    }
}