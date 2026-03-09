package com.example.onemarket.presentation.favorites

import androidx.lifecycle.ViewModel
import com.example.onemarket.data.local.FavoritesManager
import com.example.onemarket.domain.model.ProductModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesManager: FavoritesManager
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<ProductModel>>(emptyList())
    val favorites: StateFlow<List<ProductModel>> = _favorites

    fun loadFavorites() {
        _favorites.value = favoritesManager.getFavorites()
    }

    fun removeFavorite(product: ProductModel) {
        favoritesManager.toggleFavorite(product)
        loadFavorites()
    }
}