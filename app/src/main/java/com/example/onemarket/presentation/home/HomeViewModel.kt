package com.example.onemarket.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onemarket.data.local.RecentlyViewedManager
import com.example.onemarket.domain.model.CategoryModel
import com.example.onemarket.domain.model.ProductModel
import com.example.onemarket.domain.usecase.GetCategoriesUseCase
import com.example.onemarket.domain.usecase.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val recentlyViewedManager: RecentlyViewedManager
) : ViewModel() {

    private val _products = MutableStateFlow<List<ProductModel>>(emptyList())
    val products: StateFlow<List<ProductModel>> = _products

    private val _categories = MutableStateFlow<List<CategoryModel>>(emptyList())
    val categories: StateFlow<List<CategoryModel>> = _categories

    private val _recentlyViewed = MutableStateFlow<List<ProductModel>>(emptyList())
    val recentlyViewed: StateFlow<List<ProductModel>> = _recentlyViewed

    init {
        getProducts()
        getCategories()
    }

    fun loadRecentlyViewed() {
        _recentlyViewed.value = recentlyViewedManager.getRecentlyViewed()
    }

    private fun getProducts() {
        viewModelScope.launch {
            _products.value = getProductsUseCase()
        }
    }

    private fun getCategories() {
        viewModelScope.launch {
            _categories.value = getCategoriesUseCase()
        }
    }
}