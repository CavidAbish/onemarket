package com.example.onemarket.data.remote.dto

import com.example.onemarket.domain.model.CategoryModel

fun CategoryDto.toCategoryModel(imageUrl: String = ""): CategoryModel {
    return CategoryModel(
        slug = slug ?: "",
        name = name ?: "",
        url = url ?: "",
        imageUrl = imageUrl
    )
}