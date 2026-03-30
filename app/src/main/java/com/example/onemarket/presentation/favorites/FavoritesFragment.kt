package com.example.onemarket.presentation.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.onemarket.R
import com.example.onemarket.data.local.FavoritesManager
import com.example.onemarket.databinding.FragmentFavoritesBinding
import com.example.onemarket.presentation.home.ProductAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModels()

    @Inject
    lateinit var favoritesManager: FavoritesManager

    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeFavorites()

        // Məhsul kataloqu düyməsi → Kataloq fragmentinə keç
        binding.btnCatalog.setOnClickListener {
            // Bottom nav-da kataloq seç
            requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                R.id.bottom_nav
            ).selectedItemId = R.id.catalogFragment
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadFavorites()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(
            favoritesManager = favoritesManager,
            onFavoriteChanged = {
                viewModel.loadFavorites()
            },
            onProductClick = { product ->
                val action = FavoritesFragmentDirections
                    .actionFavoritesFragmentToProductDetailFragment(product)
                findNavController().navigate(action)
            }
        )
        binding.recyclerViewFavorites.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewFavorites.adapter = productAdapter
    }

    private fun observeFavorites() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favorites.collect { favoriteList ->
                productAdapter.submitList(favoriteList)

                if (favoriteList.isEmpty()) {
                    // Boş vəziyyət
                    binding.emptyStateLayout.visibility = View.VISIBLE
                    binding.btnCatalog.visibility = View.VISIBLE
                    binding.recyclerViewFavorites.visibility = View.GONE
                } else {
                    // Dolu vəziyyət
                    binding.emptyStateLayout.visibility = View.GONE
                    binding.btnCatalog.visibility = View.GONE
                    binding.recyclerViewFavorites.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}