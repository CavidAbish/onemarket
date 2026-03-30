package com.example.onemarket.presentation.home

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onemarket.R
import com.example.onemarket.data.local.FavoritesManager
import com.example.onemarket.databinding.ItemProductBinding
import com.example.onemarket.domain.model.ProductModel

class ProductAdapter(
    private val favoritesManager: FavoritesManager,
    private val onFavoriteChanged: (() -> Unit)? = null,
    private val onProductClick: ((ProductModel) -> Unit)? = null,
    private val onAddToCart: ((ProductModel) -> Unit)? = null
) : ListAdapter<ProductModel, ProductAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductModel) {
            binding.tvProductName.text = product.title
            binding.tvPrice.text = "${product.price} ₼"
            binding.tvOldPrice.text = "${product.originalPrice} ₼"
            binding.tvOldPrice.paintFlags =
                binding.tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            binding.tvDiscount.text = "-${product.discountPercentage.toInt()}%"
            binding.tvMonthlyPayment.text = "${product.monthlyPayment} ₼ x 12 ay"
            binding.ratingBar.rating = product.rating.toFloat()
            binding.tvRatingCount.text = "${product.stock} rəy"

            Glide.with(binding.root)
                .load(product.thumbnail)
                .centerCrop()
                .into(binding.ivProductImage)

            updateFavoriteIcon(product.id)

            binding.ivFavorite.setOnClickListener {
                favoritesManager.toggleFavorite(product)
                updateFavoriteIcon(product.id)
                onFavoriteChanged?.invoke()
            }

            binding.root.setOnClickListener {
                onProductClick?.invoke(product)
            }

            // Səbətə əlavə et düyməsi (əgər item_product.xml-də varsa)
            binding.btnAddToCart?.setOnClickListener {
                onAddToCart?.invoke(product)
            }
        }

        private fun updateFavoriteIcon(productId: Int) {
            if (favoritesManager.isFavorite(productId)) {
                binding.ivFavorite.setImageResource(R.drawable.ic_heart_red)
            } else {
                binding.ivFavorite.setImageResource(R.drawable.ic_heart_black)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<ProductModel>() {
        override fun areItemsTheSame(oldItem: ProductModel, newItem: ProductModel) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ProductModel, newItem: ProductModel) =
            oldItem == newItem
    }
}