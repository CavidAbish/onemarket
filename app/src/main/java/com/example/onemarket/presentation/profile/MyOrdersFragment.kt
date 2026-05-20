package com.example.onemarket.presentation.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onemarket.R
import com.example.onemarket.data.local.Order
import com.example.onemarket.data.local.OrderManager
import com.example.onemarket.databinding.FragmentMyOrdersBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyOrdersFragment : Fragment() {

    private var _binding: FragmentMyOrdersBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var orderManager: OrderManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        val orders = orderManager.getOrders()

        if (orders.isEmpty()) {
            binding.emptyLayout.visibility = View.VISIBLE
            binding.recyclerViewOrders.visibility = View.GONE
        } else {
            binding.emptyLayout.visibility = View.GONE
            binding.recyclerViewOrders.visibility = View.VISIBLE
            binding.recyclerViewOrders.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerViewOrders.adapter = OrdersAdapter(orders)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class OrdersAdapter(private val orders: List<Order>) :
        RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvOrderNumber: TextView = view.findViewById(R.id.tvOrderNumber)
            val ivProductImage: ImageView = view.findViewById(R.id.ivProductImage)
            val tvOrderAmount: TextView = view.findViewById(R.id.tvOrderAmount)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_my_order, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val order = orders[position]
            holder.tvOrderNumber.text = "№ ${order.id}"
            holder.tvOrderAmount.text = String.format("%.2f ₼", order.totalAmount)
            Glide.with(holder.itemView.context)
                .load(order.product.thumbnail)
                .centerCrop()
                .into(holder.ivProductImage)
        }

        override fun getItemCount() = orders.size
    }
}