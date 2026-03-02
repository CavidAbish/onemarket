package com.example.onemarket.domain.usecase

import com.example.onemarket.data.repository.ProductRepositoryImpl
import com.example.onemarket.domain.model.ProductModel
import com.example.onemarket.domain.repository.ProductRepository

class GetProductsUseCase(
    private val repository: ProductRepository = ProductRepositoryImpl()
) {
    suspend operator fun invoke(): List<ProductModel> {
        return repository.getProducts()
    }
}