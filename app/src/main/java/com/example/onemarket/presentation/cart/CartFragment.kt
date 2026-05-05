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
import androidx.recyclerview.widget.RecyclerView
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
    private lateinit var forYouAdapter: ProductAdapter
    private lateinit var recommendedAdapter: RecentlyViewedAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCartRecyclerView()
        setupRecentlyViewedRecyclerView()
        setupForYouRecyclerView()
        setupRecommendedRecyclerView()
        setupInfiniteScroll()
        observeRecommended()

        binding.btnCatalog.setOnClickListener {
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
                .selectedItemId = R.id.catalogFragment
        }
        binding.btnCheckout.setOnClickListener {
            findNavController().navigate(
                CartFragmentDirections.actionCartFragmentToDeliveryFragment()
            )
        }
        binding.btnCheckoutCredit.setOnClickListener {
            Toast.makeText(requireContext(), "Kreditlə sifariş rəsmiləşdirilir...", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshCart()
    }

    private fun refreshCart() {
        val cartItems = cartManager.getCartItems()
        cartAdapter.submitList(cartItems.toList())

        if (cartItems.isEmpty()) {
            // BOŞ VƏZİYYƏT
            binding.emptyCartCard.visibility = View.VISIBLE
            binding.recyclerViewCart.visibility = View.GONE
            binding.bottomCheckout.visibility = View.GONE
            binding.tvRecommended.visibility = View.GONE
            binding.recyclerViewRecommended.visibility = View.GONE

            // Əvvəl baxdıqlarını göstər
            val recent = recentlyViewedManager.getRecentlyViewed()
            if (recent.isNotEmpty()) {
                recentlyViewedAdapter.submitList(recent)
                binding.tvRecentlyViewed.visibility = View.VISIBLE
                binding.recyclerViewRecentlyViewed.visibility = View.VISIBLE
            } else {
                binding.tvRecentlyViewed.visibility = View.GONE
                binding.recyclerViewRecentlyViewed.visibility = View.GONE
            }

            // Sizin üçün seçdik
            binding.tvForYou.visibility = View.VISIBLE
            binding.recyclerViewForYou.visibility = View.VISIBLE
            viewModel.loadRecommended("fragrances", emptyList())

        } else {
            // DOLU VƏZİYYƏT
            binding.emptyCartCard.visibility = View.GONE
            binding.recyclerViewCart.visibility = View.VISIBLE
            binding.bottomCheckout.visibility = View.VISIBLE
            binding.tvRecentlyViewed.visibility = View.GONE
            binding.recyclerViewRecentlyViewed.visibility = View.GONE
            binding.tvForYou.visibility = View.GONE
            binding.recyclerViewForYou.visibility = View.GONE

            val total = cartManager.getTotalPrice()
            val count = cartManager.getItemCount()
            binding.tvOrderSummary.text = "Sifarişin məbləği ($count məhsul):"
            binding.tvTotalPrice.text = String.format("%.2f ₼", total)

            val firstCategory = cartItems.first().product.category
            val excludeIds = cartItems.map { it.product.id }
            viewModel.loadRecommended(firstCategory, excludeIds)
        }

        recommendedAdapter.notifyDataSetChanged()
        recentlyViewedAdapter.notifyDataSetChanged()
    }

    private fun setupCartRecyclerView() {
        cartAdapter = CartAdapter(cartManager = cartManager, onCartChanged = { refreshCart() })
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCart.adapter = cartAdapter
    }

    private fun setupRecentlyViewedRecyclerView() {
        recentlyViewedAdapter = RecentlyViewedAdapter(
            favoritesManager = favoritesManager,
            cartManager = cartManager,
            onProductClick = { product ->
                findNavController().navigate(CartFragmentDirections.actionCartFragmentToProductDetailFragment(product))
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

    private fun setupForYouRecyclerView() {
        forYouAdapter = ProductAdapter(
            favoritesManager = favoritesManager,
            cartManager = cartManager,
            onProductClick = { product ->
                findNavController().navigate(CartFragmentDirections.actionCartFragmentToProductDetailFragment(product))
            },
            onAddToCart = { product ->
                cartManager.addToCart(product)
                refreshCart()
                Toast.makeText(requireContext(), "${product.title} səbətə əlavə edildi", Toast.LENGTH_SHORT).show()
            }
        )
        binding.recyclerViewForYou.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewForYou.adapter = forYouAdapter
    }

    private fun setupRecommendedRecyclerView() {
        recommendedAdapter = RecentlyViewedAdapter(
            favoritesManager = favoritesManager,
            cartManager = cartManager,
            onProductClick = { product ->
                findNavController().navigate(CartFragmentDirections.actionCartFragmentToProductDetailFragment(product))
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

    private fun setupInfiniteScroll() {
        binding.recyclerViewRecommended.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val lm = recyclerView.layoutManager as LinearLayoutManager
                if (!viewModel.isLoading.value && viewModel.hasMore.value
                    && lm.findLastVisibleItemPosition() >= lm.itemCount - 3
                ) {
                    viewModel.loadNextPage()
                }
            }
        })
    }

    private fun observeRecommended() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.recommended.collect { products ->
                val cartItems = cartManager.getCartItems()
                if (cartItems.isEmpty()) {
                    // Boş vəziyyət — grid şəklində göstər
                    forYouAdapter.submitList(products)
                } else {
                    // Dolu vəziyyət — horizontal göstər
                    recommendedAdapter.submitList(products)
                    if (products.isNotEmpty()) {
                        binding.tvRecommended.visibility = View.VISIBLE
                        binding.recyclerViewRecommended.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}