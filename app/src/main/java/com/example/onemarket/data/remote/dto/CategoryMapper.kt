package com.example.onemarket.data.remote.dto

import com.example.onemarket.domain.model.CategoryModel

fun CategoryDto.toCategoryModel(): CategoryModel {
    return CategoryModel(
        slug = slug ?: "",
        name = name ?: "",
        url = url ?: ""
    )
}