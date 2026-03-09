package com.example.onemarket.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        observeData()
        setupScrollBehavior()
        setupBannerClicks()
    }

    private fun setupProductRecyclerView() {
        productAdapter = ProductAdapter(favoritesManager)
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