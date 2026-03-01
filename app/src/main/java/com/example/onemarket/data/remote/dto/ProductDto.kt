package com.example.onemarket.data.remote.dto

data class ProductResponseDto(
	val total: Int? = null,
	val limit: Int? = null,
	val skip: Int? = null,
	val products: List<ProductDto>? = null
)

data class ProductDto(
	val id: Int? = null,
	val title: String? = null,
	val price: Double? = null,
	val rating: Double? = null,
	val description: String? = null,
	val thumbnail: String? = null,
	val stock: Int? = null,
	val brand: String? = null,
	val category: String? = null,
	val discountPercentage: Double? = null,
	val images: List<String>? = null,
	val tags: List<String>? = null,
	val sku: String? = null,
	val weight: Int? = null,
	val minimumOrderQuantity: Int? = null,
	val warrantyInformation: String? = null,
	val shippingInformation: String? = null,
	val availabilityStatus: String? = null,
	val returnPolicy: String? = null,
	val reviews: List<ReviewDto>? = null,
	val meta: MetaDto? = null,
	val dimensions: DimensionsDto? = null
)

data class ReviewDto(
	val date: String? = null,
	val reviewerName: String? = null,
	val reviewerEmail: String? = null,
	val rating: Int? = null,
	val comment: String? = null
)

data class MetaDto(
	val createdAt: String? = null,
	val qrCode: String? = null,
	val barcode: String? = null,
	val updatedAt: String? = null
)

data class DimensionsDto(
	val depth: Double? = null,
	val width: Double? = null,
	val height: Double? = null
)