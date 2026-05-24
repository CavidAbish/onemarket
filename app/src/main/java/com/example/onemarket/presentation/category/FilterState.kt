package com.example.onemarket.presentation.category

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class SortOption {
    POPULAR, CHEAPEST, EXPENSIVE, BIGGEST_DISCOUNT, NEWEST;

    fun label() = when (this) {
        POPULAR -> "Populyar"
        CHEAPEST -> "Ucuz"
        EXPENSIVE -> "Baha"
        BIGGEST_DISCOUNT -> "Ən böyük endirimli"
        NEWEST -> "Yeniliklər"
    }
}

@Parcelize
data class FilterState(
    val sortBy: SortOption = SortOption.POPULAR,
    val minPrice: Double = 0.0,
    val maxPrice: Double = 0.0,
    val brand: String = "",
    val discountOnly: Boolean = false,
    val seller: String = ""
) : Parcelable {

    val hasPriceFilter: Boolean get() = minPrice > 0 || maxPrice > 0
    val hasBrandFilter: Boolean get() = brand.isNotEmpty()
    val hasSellerFilter: Boolean get() = seller.isNotEmpty()

    val activeCount: Int
        get() {
            var count = 0
            if (hasPriceFilter) count++
            if (hasBrandFilter) count++
            if (discountOnly) count++
            if (hasSellerFilter) count++
            return count
        }

    fun activeChips(): List<Pair<String, String>> {
        val chips = mutableListOf<Pair<String, String>>()
        if (hasBrandFilter) chips.add("brand" to brand)
        if (hasPriceFilter) {
            val label = "${minPrice.toInt()} - ${maxPrice.toInt()} ₼"
            chips.add("price" to label)
        }
        if (discountOnly) chips.add("discount" to "Endirimlə")
        if (hasSellerFilter) chips.add("seller" to seller)
        return chips
    }
}
