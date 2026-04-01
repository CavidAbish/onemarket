package com.example.onemarket.presentation.detail

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.onemarket.R
import com.example.onemarket.data.local.CartManager
import com.example.onemarket.data.local.FavoritesManager
import com.example.onemarket.data.local.RecentlyViewedManager
import com.example.onemarket.databinding.FragmentProductDetailBinding
import com.example.onemarket.presentation.home.ProductAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    private val args: ProductDetailFragmentArgs by navArgs()
    private val viewModel: ProductDetailViewModel by viewModels()

    @Inject lateinit var favoritesManager: FavoritesManager
    @Inject lateinit var cartManager: CartManager
    @Inject lateinit var recentlyViewedManager: RecentlyViewedManager

    private lateinit var relatedAdapter: ProductAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product
        recentlyViewedManager.addProduct(product)

        // Şəkil
        Glide.with(this).load(product.thumbnail).centerCrop().into(binding.ivProductImage)

        // Məlumatlar
        binding.tvProductName.text = product.title
        binding.ratingBar.rating = product.rating.toFloat()
        binding.ratingBarLarge.rating = product.rating.toFloat()
        binding.tvRatingCount.text = "${product.stock} rəy"
        binding.tvRatingValue.text = product.rating.toString()
        binding.tvReviewCount.text = "${product.stock} istifadəçi qiymətləndirməsi"
        binding.tvPrice.text = "${product.price} ₼"
        binding.tvOldPrice.text = "${product.originalPrice} ₼"
        binding.tvOldPrice.paintFlags = binding.tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        binding.tvMonthlyPayment.text = "${product.monthlyPayment} ₼ x 12 ay"
        binding.tvTaksit.text = "Taksitli ödəniş ${product.monthlyPayment} ₼ x 12 ay"
        binding.tvBrand.text = product.brand.ifEmpty { "—" }
        binding.tvCategory.text = product.category.ifEmpty { "—" }
        binding.tvStock.text = "${product.stock} ədəd"
        binding.tvDescription.text = product.description

        // Ürək
        updateFavoriteIcon()
        binding.ivFavorite.setOnClickListener {
            favoritesManager.toggleFavorite(product)
            updateFavoriteIcon()
        }

        // Geri
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        // Səbətə əlavə et
        updateCartButton()
        binding.btnAddToCart.setOnClickListener {
            cartManager.addToCart(product)
            updateCartButton()
            relatedAdapter.notifyDataSetChanged()
            Toast.makeText(requireContext(), "${product.title} səbətə əlavə edildi", Toast.LENGTH_SHORT).show()
        }

        // Oxşar məhsullar
        setupRelatedProducts()
        observeRelatedProducts()
        viewModel.loadRelatedProducts(product.category, product.id)
    }

    private fun updateFavoriteIcon() {
        binding.ivFavorite.setImageResource(
            if (favoritesManager.isFavorite(args.product.id)) R.drawable.ic_heart_red
            else R.drawable.ic_heart_black
        )
    }

    private fun updateCartButton() {
        val inCart = cartManager.isInCart(args.product.id)
        if (inCart) {
            binding.btnAddToCart.text = "Səbətdə"
            binding.btnAddToCart.setTextColor(ContextCompat.getColor(requireContext(), R.color.cart_green))
            binding.btnAddToCart.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.cart_green_bg)
        } else {
            binding.btnAddToCart.text = "Səbətə əlavə etmək"
            binding.btnAddToCart.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            binding.btnAddToCart.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.pink_main)
        }
    }

    private fun setupRelatedProducts() {
        relatedAdapter = ProductAdapter(
            favoritesManager = favoritesManager,
            cartManager = cartManager,
            onProductClick = { product ->
                findNavController().navigate(
                    ProductDetailFragmentDirections.actionProductDetailFragmentSelf(product)
                )
            },
            onAddToCart = { product ->
                cartManager.addToCart(product)
                relatedAdapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "${product.title} səbətə əlavə edildi", Toast.LENGTH_SHORT).show()
            }
        )
        binding.recyclerViewRelated.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewRelated.adapter = relatedAdapter
    }

    private fun observeRelatedProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.relatedProducts.collect { relatedAdapter.submitList(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}