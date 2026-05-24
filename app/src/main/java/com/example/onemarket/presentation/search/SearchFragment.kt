package com.example.onemarket.presentation.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onemarket.data.local.CartManager
import com.example.onemarket.data.local.FavoritesManager
import com.example.onemarket.databinding.FragmentSearchBinding
import com.example.onemarket.presentation.home.ProductAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()

    @Inject lateinit var favoritesManager: FavoritesManager
    @Inject lateinit var cartManager: CartManager

    private lateinit var historyAdapter: SearchHistoryAdapter
    private lateinit var resultsAdapter: ProductAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupHistoryRecyclerView()
        setupResultsRecyclerView()
        setupSearchInput()
        observeData()

        // Klaviatura avtomatik aç
        binding.etSearch.requestFocus()
        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE)
                as InputMethodManager
        imm.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnClear.setOnClickListener {
            binding.etSearch.text?.clear()
        }

        binding.tvClearHistory.setOnClickListener {
            viewModel.clearHistory()
        }
    }

    private fun setupHistoryRecyclerView() {
        historyAdapter = SearchHistoryAdapter { query ->
            binding.etSearch.setText(query)
            binding.etSearch.setSelection(query.length)
            viewModel.search(query)
        }
        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewHistory.adapter = historyAdapter
    }

    private fun setupResultsRecyclerView() {
        resultsAdapter = ProductAdapter(
            favoritesManager = favoritesManager,
            cartManager = cartManager,
            onProductClick = { product ->
                // Axtarışı tarixçəyə əlavə et
                val query = binding.etSearch.text?.toString() ?: ""
                if (query.isNotBlank()) viewModel.saveToHistory(query)

                findNavController().navigate(
                    SearchFragmentDirections.actionSearchFragmentToProductDetailFragment(product)
                )
            },
            onAddToCart = { product ->
                cartManager.addToCart(product)
                resultsAdapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "${product.title} səbətə əlavə edildi", Toast.LENGTH_SHORT).show()
            },
            onGoToCart = {
                requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                    com.example.onemarket.R.id.bottom_nav
                ).selectedItemId = com.example.onemarket.R.id.cartFragment
            }
        )
        binding.recyclerViewResults.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewResults.adapter = resultsAdapter
    }

    private fun setupSearchInput() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: ""
                binding.btnClear.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE
                viewModel.search(query)
            }
        })

        // Enter basıldıqda tarixçəyə əlavə et
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.etSearch.text?.toString() ?: ""
                if (query.isNotBlank()) {
                    viewModel.saveToHistory(query)
                    // Klaviaturanı bağla
                    val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE)
                            as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
                }
                true
            } else false
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { loading ->
                binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isSearching.collect { searching ->
                if (!searching) {
                    // Axtarış yoxdur — tarixçəni göstər
                    showHistory()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.results.collect { results ->
                resultsAdapter.submitList(results)
                if (viewModel.isSearching.value) {
                    if (results.isEmpty() && !viewModel.isLoading.value) {
                        showEmpty()
                    } else if (results.isNotEmpty()) {
                        showResults()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.history.collect { history ->
                historyAdapter.submitList(history)
                if (!viewModel.isSearching.value) {
                    binding.historyLayout.visibility = if (history.isNotEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun showHistory() {
        binding.historyLayout.visibility =
            if (viewModel.history.value.isNotEmpty()) View.VISIBLE else View.GONE
        binding.resultsLayout.visibility = View.GONE
        binding.emptyLayout.visibility = View.GONE
    }

    private fun showResults() {
        binding.historyLayout.visibility = View.GONE
        binding.resultsLayout.visibility = View.VISIBLE
        binding.emptyLayout.visibility = View.GONE
    }

    private fun showEmpty() {
        binding.historyLayout.visibility = View.GONE
        binding.resultsLayout.visibility = View.GONE
        binding.emptyLayout.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}