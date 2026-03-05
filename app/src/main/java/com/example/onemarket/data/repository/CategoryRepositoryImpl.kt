package com.example.onemarket.data.repository

import com.example.onemarket.data.remote.datasource.CategoryRemoteDataSource
import com.example.onemarket.data.remote.dto.toCategoryModel
import com.example.onemarket.domain.model.CategoryModel
import com.example.onemarket.domain.repository.CategoryRepository
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val dataSource: CategoryRemoteDataSource
) : CategoryRepository {

    override suspend fun getCategories(): List<CategoryModel> {
        return dataSource.getCategories()
            .map { (categoryDto, imageUrl) ->
                categoryDto.toCategoryModel(imageUrl)
            }
    }
}