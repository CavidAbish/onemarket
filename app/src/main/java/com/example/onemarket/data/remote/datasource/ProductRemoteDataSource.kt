package com.example.onemarket.data.remote.datasource

import com.example.onemarket.data.remote.RetrofitClient
import com.example.onemarket.data.remote.api.ProductApi
import com.example.onemarket.data.remote.dto.ProductDto


//Butun mehsullari getirmek ucun
class ProductRemoteDataSource(
    private val api: ProductApi = RetrofitClient.productApi
) {
    suspend fun getProducts(): List<ProductDto> {
        return try {
            api.getProducts().products
                ?. filterNotNull()
                ?: emptyList()
        } catch (ex: Exception) {
            emptyList()
        }
    }

//1 mehsul getirmek ucun (clickledikde falan detail hissesinin acilmasi ucun ist olunacaq)
    suspend fun getProductById(id: Int): ProductDto? {
        return try {
            api.getProductById(id)
        } catch (e: Exception) {
            null
        }
    }

}