package com.example.onemarket.di

import ProductRemoteDataSource
import com.example.onemarket.data.remote.api.ProductApi
import com.example.onemarket.data.repository.ProductRepositoryImpl
import com.example.onemarket.domain.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideProductRemoteDataSource(
        api: ProductApi  // ← ProductApi import lazımdır
    ): ProductRemoteDataSource {
        return ProductRemoteDataSource(api)
    }

    @Provides
    @Singleton
    fun provideProductRepository(
        dataSource: ProductRemoteDataSource
    ): ProductRepository {
        return ProductRepositoryImpl(dataSource)
    }
}