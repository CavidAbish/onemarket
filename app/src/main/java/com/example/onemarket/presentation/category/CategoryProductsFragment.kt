package com.example.onemarket.presentation.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
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

    @Inject lateinit var favoritesManager: FavoritesManager
    @Inject lateinit var cartManager: CartManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCategoryProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvCategoryTitle.text = args.categoryName
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        setupRecyclerView()
        observeProducts()
        viewModel.getProductsByCategory(args.slug)
    }

    override fun onResume() {
        super.onResume()
        productAdapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView() {
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
                Toast.makeText(requireContext(), "${product.title} səbətə əlavə edildi", Toast.LENGTH_SHORT).show()
            }
        )
        binding.recyclerViewCategoryProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewCategoryProducts.adapter = productAdapter
    }

    private fun observeProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.products.collect { productAdapter.submitList(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}