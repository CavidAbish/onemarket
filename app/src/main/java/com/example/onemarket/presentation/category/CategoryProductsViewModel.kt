package com.example.onemarket.presentation.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onemarket.domain.model.CategoryModel
import com.example.onemarket.domain.model.ProductModel
import com.example.onemarket.domain.usecase.GetCategoriesUseCase
import com.example.onemarket.domain.usecase.GetProductsByCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryProductsViewModel @Inject constructor(
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _allProducts = MutableStateFlow<List<ProductModel>>(emptyList())
    private val _filterState = MutableStateFlow(FilterState())

    val filterState: StateFlow<FilterState> = _filterState

    val products: StateFlow<List<ProductModel>> = combine(_allProducts, _filterState) { all, filter ->
        applyFilter(all, filter)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories = MutableStateFlow<List<CategoryModel>>(emptyList())

    fun getProductsByCategory(slug: String) {
        viewModelScope.launch {
            _allProducts.value = getProductsByCategoryUseCase(slug)
            try { categories.value = getCategoriesUseCase() } catch (_: Exception) {}
        }
    }

    fun updateFilter(state: FilterState) { _filterState.value = state }

    fun clearFilters() { _filterState.value = FilterState(sortBy = _filterState.value.sortBy) }

    fun setSortOption(option: SortOption) {
        _filterState.value = _filterState.value.copy(sortBy = option)
    }

    fun removeChip(key: String) {
        _filterState.value = when (key) {
            "brand"    -> _filterState.value.copy(brand = "")
            "price"    -> _filterState.value.copy(minPrice = 0.0, maxPrice = 0.0)
            "discount" -> _filterState.value.copy(discountOnly = false)
            "seller"   -> _filterState.value.copy(seller = "")
            else       -> _filterState.value
        }
    }

    fun getAvailableBrands(): List<String> =
        _allProducts.value.map { it.brand }.filter { it.isNotEmpty() }.distinct().sorted()

    fun getTotalCount(): Int = _allProducts.value.size

    private fun applyFilter(products: List<ProductModel>, filter: FilterState): List<ProductModel> {
        var result = products

        if (filter.minPrice > 0) result = result.filter { it.price >= filter.minPrice }
        if (filter.maxPrice > 0) result = result.filter { it.price <= filter.maxPrice }
        if (filter.brand.isNotEmpty()) result = result.filter {
            it.brand.equals(filter.brand, ignoreCase = true)
        }
        if (filter.discountOnly) result = result.filter { it.discountPercentage > 0 }
        if (filter.seller.isNotEmpty()) result = result.filter {
            it.brand.equals(filter.seller, ignoreCase = true)
        }

        result = when (filter.sortBy) {
            SortOption.POPULAR          -> result
            SortOption.CHEAPEST         -> result.sortedBy { it.price }
            SortOption.EXPENSIVE        -> result.sortedByDescending { it.price }
            SortOption.BIGGEST_DISCOUNT -> result.sortedByDescending { it.discountPercentage }
            SortOption.NEWEST           -> result.reversed()
        }
        return result
    }
}
