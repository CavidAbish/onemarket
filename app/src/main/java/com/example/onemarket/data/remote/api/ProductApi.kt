package com.example.onemarket.data.remote.api

import com.example.onemarket.data.remote.dto.ProductDto
import com.example.onemarket.data.remote.dto.ProductResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductApi{

//Umumi productlari getirir
@GET ("products")
suspend fun getProducts(): ProductResponseDto



//secilmis idli mehsulu getirir
@GET("products/{id}")
suspend fun getProductById(@Path("id") id:Int): ProductDto


//categoryyada secim etme
    @GET("products/category/{slug}")
    suspend fun getProductsByCategory(@Path("slug") slug: String): ProductResponseDto
}
