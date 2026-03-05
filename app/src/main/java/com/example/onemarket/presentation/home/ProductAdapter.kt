package com.example.onemarket.presentation.home

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onemarket.databinding.ItemProductBinding
import com.example.onemarket.domain.model.ProductModel

class ProductAdapter : ListAdapter<ProductModel, ProductAdapter.ViewHolder>(DiffCallback()) {

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