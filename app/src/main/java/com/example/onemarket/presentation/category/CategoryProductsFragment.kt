package com.example.onemarket.presentation.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onemarket.R
import com.example.onemarket.data.local.CartManager
import com.example.onemarket.data.local.FavoritesManager
import com.example.onemarket.databinding.FragmentCategoryProductsBinding
import com.example.onemarket.presentation.home.ProductAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CategoryProductsFragment : Fragment() {

    private var _binding: FragmentCategoryProductsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoryProductsViewModel by viewModels()
    private val args: CategoryProductsFragmentArgs by navArgs()
    private lateinit var productAdapter: ProductAdapter
    private lateinit var subcategoryAdapter: SubcategoryAdapter

    @Inject lateinit var favoritesManager: FavoritesManager
    @Inject lateinit var cartManager: CartManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvCategoryTitle.text = args.categoryName
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.tvSearchHint.setOnClickListener {
            findNavController().navigate(
                CategoryProductsFragmentDirections.actionCategoryProductsFragmentToSearchFragment()
            )
        }

        setupSubcategoryRecyclerView()
        setupProductRecyclerView()
        setupFilterBar()
        observeViewModel()
        setupFragmentResults()

        viewModel.getProductsByCategory(args.slug)
    }

    override fun onResume() {
        super.onResume()
        productAdapter.notifyDataSetChanged()
    }

    private fun setupSubcategoryRecyclerView() {
        subcategoryAdapter = SubcategoryAdapter { category ->
            // Navigate to same fragment with new category (pop current, push new)
            findNavController().navigate(
                R.id.action_categoryProductsFragment_self,
                Bundle().apply {
                    putString("slug", category.slug)
                    putString("categoryName", category.name)
                }
            )
        }
        binding.rvSubcategories.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = subcategoryAdapter
        }
    }

    private fun setupProductRecyclerView() {
        productAdapter = ProductAdapter(
            favoritesManager = favoritesManager,
            cartManager = cartManager,
            onProductClick = { product ->
                findNavController().navigate(
                    CategoryProductsFragmentDirections
                        .actionCategoryProductsFragmentToProductDetailFragment(product)
                )
            },
            onAddToCart = { product ->
                cartManager.addToCart(product)
                productAdapter.notifyDataSetChanged()
                Toast.makeText(
                    requireContext(), "${product.title} səbətə əlavə edildi", Toast.LENGTH_SHORT
                ).show()
            },
            onGoToCart = {
                requireActivity()
                    .findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                        R.id.bottom_nav
                    ).selectedItemId = R.id.cartFragment
            }
        )
        binding.recyclerViewCategoryProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupFilterBar() {

        binding.btnSort.setOnClickListener {
            val current = viewModel.filterState.value.sortBy
            SortBottomSheetFragment.newInstance(current)
                .show(parentFragmentManager, SortBottomSheetFragment.TAG)
        }


        binding.btnFilter.setOnClickListener {
            navigateToFilter()
        }


        binding.chipBrend.setOnClickListener {
            val filter = viewModel.filterState.value
            val brands = viewModel.getAvailableBrands()
            BrandFilterBottomSheet.newInstance(brands, filter.brand)
                .show(parentFragmentManager, BrandFilterBottomSheet.TAG)
        }


        binding.chipQiymet.setOnClickListener {
            val filter = viewModel.filterState.value
            PriceFilterBottomSheet.newInstance(filter.minPrice, filter.maxPrice)
                .show(parentFragmentManager, PriceFilterBottomSheet.TAG)
        }


        binding.chipEndiriml.setOnClickListener {
            val current = viewModel.filterState.value
            viewModel.updateFilter(current.copy(discountOnly = !current.discountOnly))
        }
    }

    private fun navigateToFilter() {
        val filter = viewModel.filterState.value
        val brands = viewModel.getAvailableBrands().joinToString(",")
        val count = viewModel.getTotalCount()

        val action = CategoryProductsFragmentDirections
            .actionCategoryProductsFragmentToFilterFragment(
                filterState = filter,
                categoryName = args.categoryName,
                availableBrands = brands,
                productCount = count
            )
        findNavController().navigate(action)
    }

    private fun setupFragmentResults() {

        parentFragmentManager.setFragmentResultListener(
            SortBottomSheetFragment.RESULT_KEY, viewLifecycleOwner
        ) { _, bundle ->
            val optionName = bundle.getString("sort_option") ?: return@setFragmentResultListener
            try {
                viewModel.setSortOption(SortOption.valueOf(optionName))
            } catch (_: Exception) {}
        }


        parentFragmentManager.setFragmentResultListener(
            FilterFragment.RESULT_KEY, viewLifecycleOwner
        ) { _, bundle ->
            val state = bundle.getParcelable<FilterState>("filter_state") ?: return@setFragmentResultListener
            viewModel.updateFilter(state)
        }


        parentFragmentManager.setFragmentResultListener(
            FilterFragment.CLEAR_KEY, viewLifecycleOwner
        ) { _, _ ->
            viewModel.clearFilters()
        }


        parentFragmentManager.setFragmentResultListener(
            PriceFilterBottomSheet.RESULT_KEY, viewLifecycleOwner
        ) { _, bundle ->
            val min = bundle.getDouble("min_price", 0.0)
            val max = bundle.getDouble("max_price", 0.0)
            viewModel.updateFilter(viewModel.filterState.value.copy(minPrice = min, maxPrice = max))
        }


        parentFragmentManager.setFragmentResultListener(
            BrandFilterBottomSheet.RESULT_KEY, viewLifecycleOwner
        ) { _, bundle ->
            val brand = bundle.getString("brand") ?: ""
            viewModel.updateFilter(viewModel.filterState.value.copy(brand = brand))
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.products.collect { products ->
                productAdapter.submitList(products)
                binding.tvProductCount.text = "${products.size} məhsul"
                val isEmpty = products.isEmpty()
                binding.recyclerViewCategoryProducts.visibility = if (isEmpty) View.GONE else View.VISIBLE
                binding.emptyStateView.visibility = if (isEmpty) View.VISIBLE else View.GONE
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categories.collect { cats ->
                subcategoryAdapter.submitList(cats)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.filterState.collect { filter ->
                updateFilterBadge(filter)
                updateActiveFilterChips(filter)
                updateDiscountChipStyle(filter)
                updatePriceChipStyle(filter)
                updateBrandChipStyle(filter)
            }
        }
    }

    private fun updateFilterBadge(filter: FilterState) {
        val count = filter.activeCount
        if (count > 0) {
            binding.tvFilterBadge.visibility = View.VISIBLE
            binding.tvFilterBadge.text = count.toString()
        } else {
            binding.tvFilterBadge.visibility = View.GONE
        }
    }

    private fun updateDiscountChipStyle(filter: FilterState) {
        if (filter.discountOnly) {
            binding.chipEndiriml.setBackgroundResource(R.drawable.bg_active_filter_chip)
            binding.tvEndirimlLabel.setTextColor(android.graphics.Color.parseColor("#E91E8C"))
        } else {
            binding.chipEndiriml.setBackgroundResource(R.drawable.bg_filter_chip)
            binding.tvEndirimlLabel.setTextColor(android.graphics.Color.parseColor("#333333"))
        }
    }

    private fun updatePriceChipStyle(filter: FilterState) {
        if (filter.hasPriceFilter) {
            binding.chipQiymet.setBackgroundResource(R.drawable.bg_active_filter_chip)
            binding.tvQiymetLabel.setTextColor(android.graphics.Color.parseColor("#E91E8C"))
        } else {
            binding.chipQiymet.setBackgroundResource(R.drawable.bg_filter_chip)
            binding.tvQiymetLabel.setTextColor(android.graphics.Color.parseColor("#333333"))
        }
    }

    private fun updateBrandChipStyle(filter: FilterState) {
        if (filter.hasBrandFilter) {
            binding.chipBrend.setBackgroundResource(R.drawable.bg_active_filter_chip)
            binding.tvBrendLabel.setTextColor(android.graphics.Color.parseColor("#E91E8C"))
        } else {
            binding.chipBrend.setBackgroundResource(R.drawable.bg_filter_chip)
            binding.tvBrendLabel.setTextColor(android.graphics.Color.parseColor("#333333"))
        }
    }

    private fun updateActiveFilterChips(filter: FilterState) {
        val chips = filter.activeChips()
        binding.llActiveFilters.removeAllViews()

        if (chips.isEmpty()) {
            binding.activeFiltersScrollView.visibility = View.GONE
            return
        }

        binding.activeFiltersScrollView.visibility = View.VISIBLE
        val inflater = LayoutInflater.from(requireContext())

        chips.forEach { (key, label) ->
            val chip = buildActiveChip(label) {
                viewModel.removeChip(key)
            }
            binding.llActiveFilters.addView(chip)
        }
    }

    private fun buildActiveChip(label: String, onRemove: () -> Unit): View {
        val dp = resources.displayMetrics.density
        val container = android.widget.LinearLayout(requireContext()).apply {
            orientation = android.widget.LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            setPadding((12 * dp).toInt(), 0, (10 * dp).toInt(), 0)
            val lp = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                (32 * dp).toInt()
            ).also { it.marginEnd = (8 * dp).toInt() }
            layoutParams = lp
            setBackgroundResource(R.drawable.bg_active_filter_chip)
        }

        val tv = TextView(requireContext()).apply {
            text = label
            textSize = 13f
            setTextColor(android.graphics.Color.parseColor("#E91E8C"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val iv = ImageView(requireContext()).apply {
            setImageResource(R.drawable.ic_close)
            val size = (18 * dp).toInt()
            layoutParams = android.widget.LinearLayout.LayoutParams(size, size)
                .also { it.marginStart = (6 * dp).toInt() }
            setOnClickListener { onRemove() }
        }

        container.addView(tv)
        container.addView(iv)
        return container
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
