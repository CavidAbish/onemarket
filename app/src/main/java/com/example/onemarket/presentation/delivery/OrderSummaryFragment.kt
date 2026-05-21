package com.example.onemarket.presentation.delivery

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.onemarket.data.local.CartManager
import com.example.onemarket.data.local.UserManager
import com.example.onemarket.databinding.FragmentOrderSummaryBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrderSummaryFragment : Fragment() {

    private var _binding: FragmentOrderSummaryBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var cartManager: CartManager
    @Inject lateinit var userManager: UserManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrderSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        // İstifadəçi məlumatları
        binding.tvContactName.text = userManager.getUserName().uppercase()
        binding.tvContactPhone.text = userManager.getUserPhone()

        // Səbət məhsulları
        // Yalnız seçilmiş məhsulları göstər
        val cartItems = cartManager.getSelectedItems().ifEmpty { cartManager.getCartItems() }
        var totalAmount = 0.0
        val deliveryCost = 3.0

        cartItems.forEachIndexed { index, item ->
            val orderView = layoutInflater.inflate(
                com.example.onemarket.R.layout.item_order_summary,
                binding.ordersContainer, false
            )

            val tvOrderNum = orderView.findViewById<android.widget.TextView>(com.example.onemarket.R.id.tvOrderNum)
            val tvProductName = orderView.findViewById<android.widget.TextView>(com.example.onemarket.R.id.tvProductName)
            val tvOrderAmount = orderView.findViewById<android.widget.TextView>(com.example.onemarket.R.id.tvOrderAmount)
            val tvDiscount = orderView.findViewById<android.widget.TextView>(com.example.onemarket.R.id.tvDiscount)
            val tvDelivery = orderView.findViewById<android.widget.TextView>(com.example.onemarket.R.id.tvDelivery)

            tvOrderNum.text = "Sifariş ${index + 1}"
            tvProductName.text = "Sifarişin məbləği (${item.quantity} məhsul):"
            tvOrderAmount.text = String.format("%.2f ₼", item.product.price * item.quantity)

            val discount = (item.product.originalPrice - item.product.price) * item.quantity
            if (discount > 0) {
                tvDiscount.text = String.format("-%.2f ₼", discount)
                tvDiscount.visibility = View.VISIBLE
            }

            tvDelivery.text = String.format("%.2f ₼", deliveryCost)

            totalAmount += item.product.price * item.quantity + deliveryCost
            binding.ordersContainer.addView(orderView)
        }

        binding.tvTotalPayment.text = String.format("%.2f ₼", totalAmount)
        binding.btnPay.text = String.format("%.2f ₼ ödə", totalAmount)

        binding.btnPay.setOnClickListener {
            findNavController().navigate(
                OrderSummaryFragmentDirections.actionOrderSummaryFragmentToPaymentFragment(totalAmount.toFloat())
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}