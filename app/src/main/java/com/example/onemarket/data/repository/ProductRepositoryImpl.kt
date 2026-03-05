package com.example.onemarket.data.repository

import com.example.onemarket.data.remote.datasource.ProductRemoteDataSource
import com.example.onemarket.data.remote.dto.toProductModel
import com.example.onemarket.domain.model.ProductModel
import com.example.onemarket.domain.repository.ProductRepository
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val dataSource: ProductRemoteDataSource
) : ProductRepository {

    override suspend fun getProducts(): List<ProductModel> {
        return dataSource.getProducts()
            .map { it.toProductModel() }
    }

    override suspend fun getProductById(id: Int): ProductModel? {
        return dataSource.getProductById(id)?.toProductModel()
    }
}