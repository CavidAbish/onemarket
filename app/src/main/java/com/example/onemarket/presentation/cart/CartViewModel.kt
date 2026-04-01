package com.example.onemarket.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onemarket.data.remote.api.CategoryApi
import com.example.onemarket.data.remote.dto.toProductModel
import com.example.onemarket.domain.model.ProductModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val categoryApi: CategoryApi
) : ViewModel() {

    private val _recommended = MutableStateFlow<List<ProductModel>>(emptyList())
    val recommended: StateFlow<List<ProductModel>> = _recommended

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _hasMore = MutableStateFlow(true)
    val hasMore: StateFlow<Boolean> = _hasMore

    private var currentCategory = ""
    private var excludeIds = listOf<Int>()
    private var currentSkip = 0
    private val pageSize = 10

    fun loadRecommended(category: String, excludeIds: List<Int>) {
        if (category != currentCategory) {
            currentCategory = category
            this.excludeIds = excludeIds
            currentSkip = 0
            _recommended.value = emptyList()
            _hasMore.value = true
        }
        loadNextPage()
    }

    fun loadNextPage() {
        if (_isLoading.value || !_hasMore.value) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = categoryApi.getProductsByCategory(
                    slug = currentCategory,
                    limit = pageSize,
                    skip = currentSkip
                )
                val newProducts = (response.products ?: emptyList())
                    .map { it.toProductModel() }
                    .filter { it.id !in excludeIds }

                if (newProducts.isEmpty() || (response.products?.size ?: 0) < pageSize) {
                    _hasMore.value = false
                }

                _recommended.value = _recommended.value + newProducts
                currentSkip += pageSize
            } catch (e: Exception) {
                _hasMore.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
}