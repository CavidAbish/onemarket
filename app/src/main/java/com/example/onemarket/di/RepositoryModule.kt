package com.example.onemarket.di

import com.example.onemarket.data.remote.api.CategoryApi
import com.example.onemarket.data.remote.datasource.ProductRemoteDataSource
import com.example.onemarket.data.remote.api.ProductApi
import com.example.onemarket.data.remote.datasource.CategoryRemoteDataSource
import com.example.onemarket.data.repository.CategoryRepositoryImpl
import com.example.onemarket.data.repository.ProductRepositoryImpl
import com.example.onemarket.domain.repository.CategoryRepository
import com.example.onemarket.domain.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {


    //Product
    @Provides
    @Singleton
    fun provideProductRemoteDataSource(api: ProductApi): ProductRemoteDataSource {
        return ProductRemoteDataSource(api)
    }
    @Provides
    @Singleton
    fun provideProductRepository(dataSource: ProductRemoteDataSource): ProductRepository {
        return ProductRepositoryImpl(dataSource)
    }

    //category

    @Provides
    @Singleton
    fun provideCategoryRemoteDataSource(api: CategoryApi): CategoryRemoteDataSource {
        return CategoryRemoteDataSource(api)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(
        dataSource: CategoryRemoteDataSource
    ): CategoryRepository {
        return CategoryRepositoryImpl(dataSource)
    }



}