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
        setupSelectAll()

        binding.btnMyOrders.setOnClickListener {
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
                .selectedItemId = R.id.profileFragment
        }

        binding.btnCatalog.setOnClickListener {
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
                .selectedItemId = R.id.catalogFragment
        }

        binding.btnCheckout.setOnClickListener {
            val selected = cartAdapter.getSelectedItems()
            if (selected.isEmpty()) {
                Toast.makeText(requireContext(), "Ödəniş üçün məhsul seçin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Seçilmiş məhsulları müvəqqəti saxla
            cartManager.saveSelectedItems(selected)
            findNavController().navigate(
                CartFragmentDirections.actionCartFragmentToDeliveryFragment()
            )
        }

        binding.btnCheckoutCredit.setOnClickListener {
            val selected = cartAdapter.getSelectedItems()
            if (selected.isEmpty()) {
                Toast.makeText(requireContext(), "Ödəniş üçün məhsul seçin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            cartManager.saveSelectedItems(selected)
            findNavController().navigate(
                CartFragmentDirections.actionCartFragmentToDeliveryFragment(isCredit = true)
            )
        }

        binding.tvDeleteSelected.setOnClickListener {
            val selected = cartAdapter.getSelectedItems()
            if (selected.isEmpty()) {
                Toast.makeText(requireContext(), "Heç bir məhsul seçilməyib", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            selected.forEach { cartManager.removeFromCart(it.product.id) }
            cartAdapter.deselectAll()
            refreshCart()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshCart()
    }

    private fun setupSelectAll() {
        binding.cbSelectAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) cartAdapter.selectAll()
            else cartAdapter.deselectAll()
        }
    }

    private fun refreshCart() {
        val cartItems = cartManager.getCartItems()
        cartAdapter.submitList(cartItems.toList())

        if (cartItems.isEmpty()) {
            binding.emptyCartCard.visibility = View.VISIBLE
            binding.recyclerViewCart.visibility = View.GONE
            binding.bottomCheckout.visibility = View.GONE
            binding.tvRecommended.visibility = View.GONE
            binding.recyclerViewRecommended.visibility = View.GONE
            binding.selectAllBar.visibility = View.GONE
            binding.selectDivider.visibility = View.GONE

            val recent = recentlyViewedManager.getRecentlyViewed()
            if (recent.isNotEmpty()) {
                recentlyViewedAdapter.submitList(recent)
                binding.tvRecentlyViewed.visibility = View.VISIBLE
                binding.recyclerViewRecentlyViewed.visibility = View.VISIBLE
            } else {
                binding.tvRecentlyViewed.visibility = View.GONE
                binding.recyclerViewRecentlyViewed.visibility = View.GONE
            }
            binding.tvForYou.visibility = View.VISIBLE
            binding.recyclerViewForYou.visibility = View.VISIBLE
            viewModel.loadRecommended("fragrances", emptyList())
        } else {
            binding.emptyCartCard.visibility = View.GONE
            binding.recyclerViewCart.visibility = View.VISIBLE
            binding.bottomCheckout.visibility = View.VISIBLE
            binding.tvRecentlyViewed.visibility = View.GONE
            binding.recyclerViewRecentlyViewed.visibility = View.GONE
            binding.tvForYou.visibility = View.GONE
            binding.recyclerViewForYou.visibility = View.GONE
            binding.selectAllBar.visibility = View.VISIBLE
            binding.selectDivider.visibility = View.VISIBLE

            val firstCategory = cartItems.first().product.category
            val excludeIds = cartItems.map { it.product.id }
            viewModel.loadRecommended(firstCategory, excludeIds)

            updateTotal()
        }

        recommendedAdapter.notifyDataSetChanged()
        recentlyViewedAdapter.notifyDataSetChanged()
    }

    private fun updateTotal() {
        val selected = cartAdapter.getSelectedItems()
        // Ən son quantity-ləri cartManager-dən götür
        val cartItems = cartManager.getCartItems()
        val total = selected.sumOf { sel ->
            val cur = cartItems.find { it.product.id == sel.product.id }
            sel.product.price * (cur?.quantity ?: sel.quantity)
        }
        val count = selected.sumOf { sel ->
            val cur = cartItems.find { it.product.id == sel.product.id }
            cur?.quantity ?: sel.quantity
        }

        binding.tvTotalPrice.text = String.format("%.2f ₼", total)
        binding.tvOrderSummary.text = "Sifarişin məbləği ($count məhsul):"

        // Seçim olmayanda buttonlar solğun olsun
        val hasSelection = selected.isNotEmpty()
        binding.btnCheckout.alpha = if (hasSelection) 1.0f else 0.4f
        binding.btnCheckout.isEnabled = hasSelection
        binding.btnCheckoutCredit.alpha = if (hasSelection) 1.0f else 0.4f
        binding.btnCheckoutCredit.isEnabled = hasSelection

        // cbSelectAll yenilə
        binding.cbSelectAll.setOnCheckedChangeListener(null)
        binding.cbSelectAll.isChecked = cartAdapter.isAllSelected()
        binding.cbSelectAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) cartAdapter.selectAll()
            else cartAdapter.deselectAll()
        }
    }

    private fun setupCartRecyclerView() {
        cartAdapter = CartAdapter(cartManager = cartManager, favoritesManager = favoritesManager, onCartChanged = {
            updateTotal()
            val cartItems = cartManager.getCartItems()
            if (cartItems.isEmpty()) refreshCart()
        })
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
                    forYouAdapter.submitList(products)
                } else {
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