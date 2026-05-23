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
import com.example.onemarket.R
import com.example.onemarket.data.local.CartManager
import com.example.onemarket.data.local.FavoritesManager
import com.example.onemarket.databinding.FragmentCatalogBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CatalogFragment : Fragment() {

    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CatalogViewModel by viewModels()
    private lateinit var catalogAdapter: CatalogAdapter

    @Inject lateinit var favoritesManager: FavoritesManager
    @Inject lateinit var cartManager: CartManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCatalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeCategories()

        binding.searchBarCard.setOnClickListener {
            findNavController().navigate(
                CatalogFragmentDirections.actionCatalogFragmentToSearchFragment()
            )
        }

        binding.btnFavorites.setOnClickListener {
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
                .selectedItemId = R.id.favoritesFragment
        }

        binding.btnCart.setOnClickListener {
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
                .selectedItemId = R.id.cartFragment
        }
    }

    override fun onResume() {
        super.onResume()
        updateBadges()
    }

    private fun updateBadges() {
        val favCount = favoritesManager.getFavorites().size
        val cartCount = cartManager.getCartItems().sumOf { it.quantity }

        binding.tvFavoritesCount.text = favCount.toString()
        binding.tvFavoritesCount.visibility = if (favCount > 0) View.VISIBLE else View.GONE

        binding.tvCartCount.text = cartCount.toString()
        binding.tvCartCount.visibility = if (cartCount > 0) View.VISIBLE else View.GONE
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