package com.example.onemarket.presentation.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.onemarket.databinding.FragmentCatalogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CatalogFragment : Fragment() {

    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CatalogViewModel by viewModels()
    private lateinit var catalogAdapter: CatalogAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCatalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeCategories()

        // Axtarışa keç
        binding.searchBarCard.setOnClickListener {
            findNavController().navigate(
                CatalogFragmentDirections.actionCatalogFragmentToSearchFragment()
            )
        }
    }

    private fun setupRecyclerView() {
        catalogAdapter = CatalogAdapter { category ->
            val action = CatalogFragmentDirections
                .actionCatalogFragmentToCategoryProductsFragment(
                    slug = category.slug,
                    categoryName = category.name
                )
            findNavController().navigate(action)
        }
        binding.recyclerViewCatalog.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerViewCatalog.adapter = catalogAdapter
    }

    private fun observeCategories() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categories.collect { categoryList ->
                catalogAdapter.submitList(categoryList)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}