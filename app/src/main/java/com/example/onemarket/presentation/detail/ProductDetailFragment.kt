package com.example.onemarket.presentation.detail

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.onemarket.R
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

    @Inject
    lateinit var favoritesManager: FavoritesManager

    @Inject
    lateinit var recentlyViewedManager: RecentlyViewedManager

    private lateinit var relatedAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product

        // Baxılan məhsulu saxla
        recentlyViewedManager.addProduct(product)

        // Şəkil
        Glide.with(this)
            .load(product.thumbnail)
            .centerCrop()
            .into(binding.ivProductImage)

        // Ad
        binding.tvProductName.text = product.title

        // Reytinq
        binding.ratingBar.rating = product.rating.toFloat()
        binding.ratingBarLarge.rating = product.rating.toFloat()
        binding.tvRatingCount.text = "${product.stock} rəy"
        binding.tvRatingValue.text = product.rating.toString()
        binding.tvReviewCount.text = "${product.stock} istifadəçi qiymətləndirməsi"

        // Qiymətlər
        binding.tvPrice.text = "${product.price} ₼"
        binding.tvOldPrice.text = "${product.originalPrice} ₼"
        binding.tvOldPrice.paintFlags =
            binding.tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        binding.tvMonthlyPayment.text = "${product.monthlyPayment} ₼ x 12 ay"
        binding.tvTaksit.text = "Taksitli ödəniş ${product.monthlyPayment} ₼ x 12 ay"

        // Xüsusiyyətlər
        binding.tvBrand.text = product.brand.ifEmpty { "—" }
        binding.tvCategory.text = product.category.ifEmpty { "—" }
        binding.tvStock.text = "${product.stock} ədəd"

        // Təsvir
        binding.tvDescription.text = product.description

        // Ürək
        updateFavoriteIcon()
        binding.ivFavorite.setOnClickListener {
            favoritesManager.toggleFavorite(product)
            updateFavoriteIcon()
        }

        // Geri
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Oxşar məhsullar
        setupRelatedProducts()
        observeRelatedProducts()
        viewModel.loadRelatedProducts(product.category, product.id)
    }

    private fun setupRelatedProducts() {
        relatedAdapter = ProductAdapter(
            favoritesManager = favoritesManager,
            onProductClick = { product ->
                val action = ProductDetailFragmentDirections
                    .actionProductDetailFragmentSelf(product)
                findNavController().navigate(action)
            }
        )
        binding.recyclerViewRelated.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewRelated.adapter = relatedAdapter
    }

    private fun observeRelatedProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.relatedProducts.collect { products ->
                relatedAdapter.submitList(products)
            }
        }
    }

    private fun updateFavoriteIcon() {
        if (favoritesManager.isFavorite(args.product.id)) {
            binding.ivFavorite.setImageResource(R.drawable.ic_heart_red)
        } else {
            binding.ivFavorite.setImageResource(R.drawable.ic_heart_black)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}