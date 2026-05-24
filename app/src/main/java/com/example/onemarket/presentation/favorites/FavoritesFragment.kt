package com.example.onemarket.presentation.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.onemarket.R
import com.example.onemarket.data.local.CartManager
import com.example.onemarket.data.local.FavoritesManager
import com.example.onemarket.data.local.UserManager
import com.example.onemarket.databinding.FragmentFavoritesBinding
import com.example.onemarket.presentation.home.ProductAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModels()

    @Inject lateinit var favoritesManager: FavoritesManager
    @Inject lateinit var cartManager: CartManager
    @Inject lateinit var userManager: UserManager

    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        binding.btnLoginBottom.setOnClickListener {
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
                .selectedItemId = R.id.profileFragment
        }

        binding.btnCatalog.setOnClickListener {
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
                .selectedItemId = R.id.catalogFragment
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        if (!userManager.isLoggedIn()) {
            // Giriş tələb olunur
            binding.loginRequiredLayout.visibility = View.VISIBLE
            binding.btnLoginBottom.visibility = View.VISIBLE
            binding.emptyStateLayout.visibility = View.GONE
            binding.btnCatalog.visibility = View.GONE
            binding.recyclerViewFavorites.visibility = View.GONE
        } else {
            binding.loginRequiredLayout.visibility = View.GONE
            binding.btnLoginBottom.visibility = View.GONE
            observeFavorites()
            viewModel.loadFavorites()
        }
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(
            favoritesManager = favoritesManager,
            cartManager = cartManager,
            onFavoriteChanged = { viewModel.loadFavorites() },
            onProductClick = { product ->
                findNavController().navigate(
                    FavoritesFragmentDirections.actionFavoritesFragmentToProductDetailFragment(product)
                )
            },
            onAddToCart = { product ->
                cartManager.addToCart(product)
                productAdapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "${product.title} səbətə əlavə edildi", Toast.LENGTH_SHORT).show()
            },
            onGoToCart = {
                requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
                    .selectedItemId = R.id.cartFragment
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
                    binding.emptyStateLayout.visibility = View.VISIBLE
                    binding.btnCatalog.visibility = View.VISIBLE
                    binding.recyclerViewFavorites.visibility = View.GONE
                } else {
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