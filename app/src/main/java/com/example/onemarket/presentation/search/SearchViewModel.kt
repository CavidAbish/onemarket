package com.example.onemarket.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onemarket.data.local.SearchManager
import com.example.onemarket.data.remote.api.ProductApi
import com.example.onemarket.data.remote.dto.toProductModel
import com.example.onemarket.domain.model.ProductModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val productApi: ProductApi,
    private val searchManager: SearchManager
) : ViewModel() {

    private val _results = MutableStateFlow<List<ProductModel>>(emptyList())
    val results: StateFlow<List<ProductModel>> = _results

    private val _history = MutableStateFlow<List<String>>(emptyList())
    val history: StateFlow<List<String>> = _history

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private var searchJob: Job? = null

    init {
        loadHistory()
    }

    fun loadHistory() {
        _history.value = searchManager.getHistory()
    }

    fun search(query: String) {
        if (query.isBlank()) {
            _isSearching.value = false
            _results.value = emptyList()
            return
        }

        _isSearching.value = true
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // debounce
            _isLoading.value = true
            try {
                val response = productApi.searchProducts(query)
                _results.value = (response.products ?: emptyList()).map { it.toProductModel() }
            } catch (e: Exception) {
                _results.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveToHistory(query: String) {
        searchManager.addToHistory(query)
        loadHistory()
    }

    fun clearHistory() {
        searchManager.clearHistory()
        loadHistory()
    }
}