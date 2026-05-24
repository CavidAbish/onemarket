package com.example.onemarket.data.local

data class Address(
    val id: Long = System.currentTimeMillis(),
    val name: String,           // e.g. "Evimin", "İş"
    val fullAddress: String,    // e.g. "Bakı, Puşkin küçəsi 17C"
    val apartment: String = "", // e.g. "Blok 2, mənzil 15"
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val isDefault: Boolean = false
)
