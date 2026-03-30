package com.example.onemarket.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onemarket.domain.model.ProductModel
import com.example.onemarket.domain.usecase.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {

    private val _products = MutableStateFlow<List<ProductModel>>(emptyList())
    val products: StateFlow<List<ProductModel>> = _products

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _products.value = getProductsUseCase()
        }
    }
}