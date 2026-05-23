package com.example.onemarket.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onemarket.data.local.OrderManager
import com.example.onemarket.databinding.FragmentMyOrdersBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyOrdersFragment : Fragment() {

    private var _binding: FragmentMyOrdersBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var orderManager: OrderManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.recyclerViewOrders.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onResume() {
        super.onResume()
        loadOrders()
    }

    private fun loadOrders() {
        val orders = orderManager.getOrders()

        if (orders.isEmpty()) {
            binding.emptyLayout.visibility = View.VISIBLE
            binding.recyclerViewOrders.visibility = View.GONE
        } else {
            binding.emptyLayout.visibility = View.GONE
            binding.recyclerViewOrders.visibility = View.VISIBLE

            binding.recyclerViewOrders.adapter = MyOrdersAdapter(orders) { order ->
                findNavController().navigate(
                    MyOrdersFragmentDirections.actionMyOrdersFragmentToOrderDetailFragment(order.id)
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}