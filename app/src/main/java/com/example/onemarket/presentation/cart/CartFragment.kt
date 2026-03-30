package com.example.onemarket.presentation.cart

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onemarket.R
import com.example.onemarket.data.local.CartManager
import com.example.onemarket.data.local.FavoritesManager
import com.example.onemarket.data.local.RecentlyViewedManager
import com.example.onemarket.databinding.FragmentCartBinding
import com.example.onemarket.presentation.home.ProductAdapter
import com.example.onemarket.presentation.home.RecentlyViewedAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CartViewModel by viewModels()

    @Inject lateinit var cartManager: CartManager
    @Inject lateinit var favoritesManager: FavoritesManager
    @Inject lateinit var recentlyViewedManager: RecentlyViewedManager

    private lateinit var cartAdapter: CartAdapter
    private lateinit var recentlyViewedAdapter: RecentlyViewedAdapter
    private lateinit var recommendedAdapter: RecentlyViewedAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCartRecyclerView()
        setupRecentlyViewedRecyclerView()
        setupRecommendedRecyclerView()
        observeRecommended()

        binding.btnCatalog.setOnClickListener {
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
                .selectedItemId = R.id.catalogFragment
        }

        binding.btnCheckout.setOnClickListener {
            Toast.makeText(requireContext(), "Sifariş rəsmiləşdirilir...", Toast.LENGTH_SHORT).show()
        }

        binding.btnCheckoutCredit.setOnClickListener {
            Toast.makeText(requireContext(), "Kreditlə sifariş rəsmiləşdirilir...", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshCart()
        loadRecentlyViewed()
    }

    private fun refreshCart() {
        val cartItems = cartManager.getCartItems()
        cartAdapter.submitList(cartItems)

        if (cartItems.isEmpty()) {
            binding.emptyCartCard.visibility = View.VISIBLE
            binding.recyclerViewCart.visibility = View.GONE
            binding.bottomCheckout.visibility = View.GONE
        } else {
            binding.emptyCartCard.visibility = View.GONE
            binding.recyclerViewCart.visibility = View.VISIBLE
            binding.bottomCheckout.visibility = View.VISIBLE

            val total = cartManager.getTotalPrice()
            val count = cartManager.getItemCount()
            binding.tvOrderSummary.text = "Sifarişin məbləği ($count məhsul):"
            binding.tvTotalPrice.text = String.format("%.2f ₼", total)
        }
    }

    private fun loadRecentlyViewed() {
        val recent = recentlyViewedManager.getRecentlyViewed()
        recentlyViewedAdapter.submitList(recent)

        if (recent.isEmpty()) {
            binding.tvRecentlyViewed.visibility = View.GONE
            binding.recyclerViewRecentlyViewed.visibility = View.GONE
        } else {
            binding.tvRecentlyViewed.visibility = View.VISIBLE
            binding.recyclerViewRecentlyViewed.visibility = View.VISIBLE
        }
    }

    private fun setupCartRecyclerView() {
        cartAdapter = CartAdapter(
            cartManager = cartManager,
            onCartChanged = { refreshCart() }
        )
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCart.adapter = cartAdapter
    }

    private fun setupRecentlyViewedRecyclerView() {
        recentlyViewedAdapter = RecentlyViewedAdapter(
            favoritesManager = favoritesManager,
            onProductClick = { product ->
                val action = CartFragmentDirections
                    .actionCartFragmentToProductDetailFragment(product)
                findNavController().navigate(action)
            },
            onAddToCart = { product ->
                cartManager.addToCart(product)
                refreshCart()
                Toast.makeText(requireContext(), "${product.title} səbətə əlavə edildi", Toast.LENGTH_SHORT).show()
            }
        )
        binding.recyclerViewRecentlyViewed.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewRecentlyViewed.adapter = recentlyViewedAdapter
    }

    private fun setupRecommendedRecyclerView() {
        recommendedAdapter = RecentlyViewedAdapter(
            favoritesManager = favoritesManager,
            onProductClick = { product ->
                val action = CartFragmentDirections
                    .actionCartFragmentToProductDetailFragment(product)
                findNavController().navigate(action)
            },
            onAddToCart = { product ->
                cartManager.addToCart(product)
                refreshCart()
                Toast.makeText(requireContext(), "${product.title} səbətə əlavə edildi", Toast.LENGTH_SHORT).show()
            }
        )
        binding.recyclerViewRecommended.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewRecommended.adapter = recommendedAdapter
    }

    private fun observeRecommended() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.products.collect { products ->
                if (products.isNotEmpty()) {
                    recommendedAdapter.submitList(products.take(10))
                    binding.tvRecommended.visibility = View.VISIBLE
                    binding.recyclerViewRecommended.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}