package com.example.onemarket.presentation.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
    private lateinit var bannerAdapter: BannerAdapter

    @Inject lateinit var favoritesManager: FavoritesManager
    @Inject lateinit var cartManager: CartManager

    // Banner auto-scroll
    private val bannerHandler = Handler(Looper.getMainLooper())
    private var bannerRunnable: Runnable? = null
    private var currentBannerPos = 0

    // Banner şəkilləri — res/drawable-a əlavə etdiyin şəkillərin id-ləri
    private val bannerImages = listOf(
        R.drawable.banner_1,
        R.drawable.banner_2,
        R.drawable.banner_3,
        R.drawable.banner_4,
        R.drawable.banner_5,
        R.drawable.banner_6,
        R.drawable.banner_7
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBannerRecyclerView()
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
        productAdapter.notifyDataSetChanged()
        recentlyViewedAdapter.notifyDataSetChanged()
        startBannerAutoScroll()
    }

    override fun onPause() {
        super.onPause()
        stopBannerAutoScroll()
    }

    private fun setupBannerRecyclerView() {
        bannerAdapter = BannerAdapter(bannerImages)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewBanners.layoutManager = layoutManager
        binding.recyclerViewBanners.adapter = bannerAdapter

        // Ortadan başla ki hər iki tərəfə scroll olsun
        currentBannerPos = bannerAdapter.getStartPosition()
        binding.recyclerViewBanners.scrollToPosition(currentBannerPos)
    }

    private fun startBannerAutoScroll() {
        bannerRunnable = Runnable {
            currentBannerPos++
            binding.recyclerViewBanners.smoothScrollToPosition(currentBannerPos)
            bannerRunnable?.let { bannerHandler.postDelayed(it, 3000) }
        }
        bannerRunnable?.let { bannerHandler.postDelayed(it, 3000) }
    }

    private fun stopBannerAutoScroll() {
        bannerRunnable?.let { bannerHandler.removeCallbacks(it) }
    }

    private fun setupProductRecyclerView() {
        productAdapter = ProductAdapter(
            favoritesManager = favoritesManager,
            cartManager = cartManager,
            onFavoriteChanged = { recentlyViewedAdapter.notifyDataSetChanged() },
            onProductClick = { product ->
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(product)
                )
            },
            onAddToCart = { product ->
                cartManager.addToCart(product)
                productAdapter.notifyDataSetChanged()
                recentlyViewedAdapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "${product.title} səbətə əlavə edildi", Toast.LENGTH_SHORT).show()
            }
        )
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = productAdapter
    }

    private fun setupCategoryRecyclerView() {
        categoryAdapter = CategoryAdapter { category ->
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToCategoryProductsFragment(
                    slug = category.slug, categoryName = category.name
                )
            )
        }
        binding.categoryRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.categoryRecyclerView.adapter = categoryAdapter
    }

    private fun setupRecentlyViewedRecyclerView() {
        recentlyViewedAdapter = RecentlyViewedAdapter(
            favoritesManager = favoritesManager,
            cartManager = cartManager,
            onProductClick = { product ->
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(product)
                )
            },
            onAddToCart = { product ->
                cartManager.addToCart(product)
                productAdapter.notifyDataSetChanged()
                recentlyViewedAdapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "${product.title} səbətə əlavə edildi", Toast.LENGTH_SHORT).show()
            },
            onFavoriteChanged = { productAdapter.notifyDataSetChanged() }
        )
        binding.recyclerViewRecentlyViewed.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewRecentlyViewed.adapter = recentlyViewedAdapter
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.products.collect { productAdapter.submitList(it) }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categories.collect { categoryAdapter.submitList(it) }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.recentlyViewed.collect { recentList ->
                val prev = recentlyViewedAdapter.itemCount
                recentlyViewedAdapter.submitList(recentList) {
                    if (recentList.isNotEmpty() && recentList.size > prev)
                        binding.recyclerViewRecentlyViewed.smoothScrollToPosition(0)
                }
                binding.tvRecentlyViewed.visibility = if (recentList.isEmpty()) View.GONE else View.VISIBLE
                binding.recyclerViewRecentlyViewed.visibility = if (recentList.isEmpty()) View.GONE else View.VISIBLE
            }
        }
    }

    private fun setupScrollBehavior() {
        binding.nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY > 150) {
                if (binding.stickyBanner.visibility == View.GONE) {
                    binding.stickyBanner.visibility = View.VISIBLE
                    binding.stickyBanner.translationY = -binding.stickyBanner.height.toFloat()
                    binding.stickyBanner.animate().translationY(0f).alpha(1f).setDuration(200).start()
                }
            } else {
                if (binding.stickyBanner.visibility == View.VISIBLE) {
                    binding.stickyBanner.animate().alpha(0f).setDuration(200).withEndAction {
                        binding.stickyBanner.visibility = View.GONE
                        binding.stickyBanner.alpha = 1f
                    }.start()
                }
            }
        }
    }

    private fun setupBannerClicks() {
        binding.infoBanner.setOnClickListener { InfoBottomSheet().show(childFragmentManager, "InfoBottomSheet") }
        binding.stickyBanner.setOnClickListener { InfoBottomSheet().show(childFragmentManager, "InfoBottomSheet") }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopBannerAutoScroll()
        _binding = null
    }
}