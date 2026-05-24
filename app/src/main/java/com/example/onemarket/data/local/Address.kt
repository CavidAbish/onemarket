package com.example.onemarket.data.local

data class Address(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val fullAddress: String,
    val apartment: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val isDefault: Boolean = false
)
