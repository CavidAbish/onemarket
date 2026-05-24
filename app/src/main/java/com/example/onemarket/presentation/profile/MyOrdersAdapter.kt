package com.example.onemarket.presentation.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
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
            binding.tvOrderNumber.text = "№ ${order.id}"
            binding.tvOrderAmount.text = String.format("%.2f ₼", order.totalAmount)

            // Status
            when {
                order.status == "cancelled" -> {
                    binding.tvOrderStatus.text = "Ləğv edildi"
                    binding.tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#F44336"))
                }
                order.status == "credit_pending" -> {
                    binding.tvOrderStatus.text = "Kredit müraciəti gözlənilir"
                    binding.tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#FF6F00"))
                }
                order.paymentMethod == "Təhvil alarkən bank kartı vasitəsi ilə" -> {
                    binding.tvOrderStatus.text = "Ödəniş gözləmədədir"
                    binding.tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"))
                }
                else -> {
                    binding.tvOrderStatus.text = "Ödəniş olunub"
                    binding.tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
                }
            }


            binding.btnCopyOrder.setOnClickListener {
                val ctx = binding.root.context
                val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("order_id", order.id.toString()))
                Toast.makeText(ctx, "Sifariş nömrəsi kopyalandı", Toast.LENGTH_SHORT).show()
            }

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