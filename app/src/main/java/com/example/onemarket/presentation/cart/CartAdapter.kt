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
import java.util.Calendar

class CartAdapter(
    private val cartManager: CartManager,
    private val onCartChanged: () -> Unit
) : ListAdapter<CartManager.CartItem, CartAdapter.ViewHolder>(DiffCallback()) {

    // Seçilmiş məhsulların id-ləri
    private val selectedIds = mutableSetOf<Int>()

    fun getSelectedItems(): List<CartManager.CartItem> =
        currentList.filter { it.product.id in selectedIds }

    fun selectAll() {
        selectedIds.addAll(currentList.map { it.product.id })
        notifyDataSetChanged()
        onCartChanged()
    }

    fun deselectAll() {
        selectedIds.clear()
        notifyDataSetChanged()
        onCartChanged()
    }

    fun isAllSelected(): Boolean =
        currentList.isNotEmpty() && currentList.all { it.product.id in selectedIds }

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

            // Çatdırılma tarixi
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, 3)
            val day = cal.get(Calendar.DAY_OF_MONTH)
            val months = listOf("yan","fev","mar","apr","may","iyn",
                "iyl","avq","sen","okt","noy","dek")
            val month = months[cal.get(Calendar.MONTH)]
            // Əgər layout-da tvDeliveryDate varsa yeniləyirik
            try {
                binding.root.findViewWithTag<android.widget.TextView>("tvDeliveryDate")
                    ?.text = "Çatdırılma: $day $month"
            } catch (_: Exception) {}

            Glide.with(binding.root)
                .load(product.thumbnail)
                .centerCrop()
                .into(binding.ivProductImage)

            // Checkbox
            binding.checkbox.setOnCheckedChangeListener(null)
            binding.checkbox.isChecked = product.id in selectedIds
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) selectedIds.add(product.id)
                else selectedIds.remove(product.id)
                onCartChanged()
            }

            binding.btnPlus.setOnClickListener {
                val newQty = item.quantity + 1
                cartManager.updateQuantity(product.id, newQty)
                binding.tvQuantity.text = newQty.toString()
                binding.tvTotalPrice.text = String.format("%.2f ₼", product.price * newQty)
                onCartChanged()
                submitList(cartManager.getCartItems())
            }

            binding.btnMinus.setOnClickListener {
                if (item.quantity > 1) {
                    val newQty = item.quantity - 1
                    cartManager.updateQuantity(product.id, newQty)
                    binding.tvQuantity.text = newQty.toString()
                    binding.tvTotalPrice.text = String.format("%.2f ₼", product.price * newQty)
                    onCartChanged()
                    submitList(cartManager.getCartItems())
                } else {
                    selectedIds.remove(product.id)
                    cartManager.removeFromCart(product.id)
                    submitList(cartManager.getCartItems())
                    onCartChanged()
                }
            }

            binding.btnRemove.setOnClickListener {
                selectedIds.remove(product.id)
                cartManager.removeFromCart(product.id)
                submitList(cartManager.getCartItems())
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