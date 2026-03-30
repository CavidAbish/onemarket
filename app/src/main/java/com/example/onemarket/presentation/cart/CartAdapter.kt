package com.example.onemarket.presentation.cart

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onemarket.data.local.CartManager
import com.example.onemarket.databinding.ItemCartProductBinding

class CartAdapter(
    private val cartManager: CartManager,
    private val onCartChanged: () -> Unit
) : ListAdapter<CartManager.CartItem, CartAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemCartProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartManager.CartItem) {
            val product = item.product

            binding.tvSellerName.text = product.brand.ifEmpty { "OneMarket" }
            binding.tvProductName.text = product.title
            binding.tvDiscount.text = "-${product.discountPercentage.toInt()}%"
            binding.tvPrice.text = "${product.price} ₼"
            binding.tvOldPrice.text = "${product.originalPrice} ₼"
            binding.tvOldPrice.paintFlags =
                binding.tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            binding.tvQuantity.text = item.quantity.toString()
            binding.tvTotalPrice.text = String.format("%.2f ₼", product.price * item.quantity)

            Glide.with(binding.root)
                .load(product.thumbnail)
                .centerCrop()
                .into(binding.ivProductImage)

            binding.btnPlus.setOnClickListener {
                cartManager.updateQuantity(product.id, item.quantity + 1)
                onCartChanged()
            }

            binding.btnMinus.setOnClickListener {
                cartManager.updateQuantity(product.id, item.quantity - 1)
                onCartChanged()
            }

            binding.btnRemove.setOnClickListener {
                cartManager.removeFromCart(product.id)
                onCartChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCartProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<CartManager.CartItem>() {
        override fun areItemsTheSame(a: CartManager.CartItem, b: CartManager.CartItem) =
            a.product.id == b.product.id

        override fun areContentsTheSame(a: CartManager.CartItem, b: CartManager.CartItem) =
            a == b
    }
}