package com.example.onemarket.domain.usecase

import com.example.onemarket.domain.model.ProductModel
import com.example.onemarket.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductsByCategoryUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(slug: String): List<ProductModel> {
        return repository.getProductsByCategory(slug)
    }
}