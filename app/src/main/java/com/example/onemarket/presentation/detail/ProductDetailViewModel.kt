package com.example.onemarket.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onemarket.domain.model.ProductModel
import com.example.onemarket.domain.usecase.GetProductsByCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase
) : ViewModel() {

    private val _relatedProducts = MutableStateFlow<List<ProductModel>>(emptyList())
    val relatedProducts: StateFlow<List<ProductModel>> = _relatedProducts

    fun loadRelatedProducts(category: String, currentProductId: Int) {
        viewModelScope.launch {
            val products = getProductsByCategoryUseCase(category)
            // Özünü siyahıdan çıxart
            _relatedProducts.value = products.filter { it.id != currentProductId }
        }
    }
}