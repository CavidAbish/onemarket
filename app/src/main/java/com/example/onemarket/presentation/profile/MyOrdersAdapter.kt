package com.example.onemarket.presentation.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onemarket.data.local.Order
import com.example.onemarket.databinding.ItemMyOrderBinding

class MyOrdersAdapter(
    private val orders: List<Order>,
    private val onClick: (Order) -> Unit
) : RecyclerView.Adapter<MyOrdersAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemMyOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            // Sifariş nömrəsi — OrderDetailFragment ilə eyni
            binding.tvOrderNumber.text = "№ ${order.id}"
            binding.tvOrderAmount.text = String.format("%.2f ₼", order.totalAmount)
            binding.tvOrderDate.text = order.date
            binding.tvOrderDate.visibility = android.view.View.VISIBLE

            Glide.with(binding.root)
                .load(order.product.thumbnail)
                .centerCrop()
                .into(binding.ivProductImage)

            binding.root.setOnClickListener { onClick(order) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMyOrderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount() = orders.size
}