package com.example.onemarket.presentation.home

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
import com.example.onemarket.data.local.FavoritesManager
import com.example.onemarket.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var productAdapter: ProductAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var recentlyViewedAdapter: RecentlyViewedAdapter

    @Inject
    lateinit var favoritesManager: FavoritesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupProductRecyclerView()
        setupCategoryRecyclerView()
        setupRecentlyViewedRecyclerView()
        observeData()
        setupScrollBehavior()
        setupBannerClicks()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadRecentlyViewed()
        // Hər iki adapteri yenilə — like dəyişmiş ola bilər
        refreshAdapters()
    }

    private fun refreshAdapters() {
        productAdapter.notifyDataSetChanged()
        recentlyViewedAdapter.notifyDataSetChanged()
    }

    private fun setupProductRecyclerView() {
        productAdapter = ProductAdapter(
            favoritesManager = favoritesManager,
            onFavoriteChanged = {
                // Like dəyişdikdə hər iki adapteri yenilə
                recentlyViewedAdapter.notifyDataSetChanged()
            },
            onProductClick = { product ->
                val action = HomeFragmentDirections
                    .actionHomeFragmentToProductDetailFragment(product)
                findNavController().navigate(action)
            }
        )
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = productAdapter
    }

    private fun setupCategoryRecyclerView() {
        categoryAdapter = CategoryAdapter { category ->
            val action = HomeFragmentDirections
                .actionHomeFragmentToCategoryProductsFragment(
                    slug = category.slug,
                    categoryName = category.name
                )
            findNavController().navigate(action)
        }
        binding.categoryRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.categoryRecyclerView.adapter = categoryAdapter
    }

    private fun setupRecentlyViewedRecyclerView() {
        recentlyViewedAdapter = RecentlyViewedAdapter(
            favoritesManager = favoritesManager,
            onProductClick = { product ->
                val action = HomeFragmentDirections
                    .actionHomeFragmentToProductDetailFragment(product)
                findNavController().navigate(action)
            },
            onAddToCart = { product ->
                Toast.makeText(
                    requireContext(),
                    "${product.title} səbətə əlavə edildi",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onFavoriteChanged = {
                // Like dəyişdikdə əsas adapteri də yenilə
                productAdapter.notifyDataSetChanged()
            }
        )
        binding.recyclerViewRecentlyViewed.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.recyclerViewRecentlyViewed.adapter = recentlyViewedAdapter
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.products.collect { productList ->
                productAdapter.submitList(productList)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categories.collect { categoryList ->
                categoryAdapter.submitList(categoryList)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.recentlyViewed.collect { recentList ->
                recentlyViewedAdapter.submitList(recentList)
                if (recentList.isEmpty()) {
                    binding.tvRecentlyViewed.visibility = View.GONE
                    binding.recyclerViewRecentlyViewed.visibility = View.GONE
                } else {
                    binding.tvRecentlyViewed.visibility = View.VISIBLE
                    binding.recyclerViewRecentlyViewed.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupScrollBehavior() {
        binding.nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY > 150) {
                if (binding.stickyBanner.visibility == View.GONE) {
                    binding.stickyBanner.visibility = View.VISIBLE
                    binding.stickyBanner.translationY = -binding.stickyBanner.height.toFloat()
                    binding.stickyBanner.animate()
                        .translationY(0f)
                        .alpha(1f)
                        .setDuration(200)
                        .start()
                }
            } else {
                if (binding.stickyBanner.visibility == View.VISIBLE) {
                    binding.stickyBanner.animate()
                        .alpha(0f)
                        .setDuration(200)
                        .withEndAction {
                            binding.stickyBanner.visibility = View.GONE
                            binding.stickyBanner.alpha = 1f
                        }
                        .start()
                }
            }
        }
    }

    private fun setupBannerClicks() {
        binding.infoBanner.setOnClickListener {
            InfoBottomSheet().show(childFragmentManager, "InfoBottomSheet")
        }
        binding.stickyBanner.setOnClickListener {
            InfoBottomSheet().show(childFragmentManager, "InfoBottomSheet")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}