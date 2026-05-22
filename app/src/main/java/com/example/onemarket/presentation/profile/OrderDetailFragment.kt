package com.example.onemarket.presentation.profile

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.onemarket.data.local.OrderManager
import com.example.onemarket.data.local.UserManager
import com.example.onemarket.databinding.FragmentOrderDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrderDetailFragment : Fragment() {

    private var _binding: FragmentOrderDetailBinding? = null
    private val binding get() = _binding!!

    private val args: OrderDetailFragmentArgs by navArgs()

    @Inject lateinit var orderManager: OrderManager
    @Inject lateinit var userManager: UserManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        val order = orderManager.getOrders().find { it.id == args.orderId } ?: return

        // Sifariş nömrəsi — MyOrdersAdapter ilə eyni
        binding.tvOrderNumber.text = "Sifariş №${order.id}"

        // Çatdırılma ünvanı
        binding.tvDeliveryAddress.text = "Bakı şəh.Nərimanov.Möhsün Sənani küç., 153"

        // Sifarişin tərkibi
        binding.tvSellerName.text = order.product.brand.ifEmpty { "OneMarket" }
        binding.tvProductTitle.text = order.product.title
        binding.tvProductPrice.text = String.format("%.2f ₼", order.product.price)
        binding.tvProductOldPrice.text = String.format("%.2f ₼", order.product.originalPrice)
        binding.tvProductOldPrice.paintFlags =
            binding.tvProductOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        binding.tvProductQuantity.text = "x ${order.quantity} ədəd."

        Glide.with(this)
            .load(order.product.thumbnail)
            .centerCrop()
            .into(binding.ivProductImage)

        // Ödəniş məlumatları
        binding.tvBuyerName.text = userManager.getUserName().ifEmpty { "Alıcı" }
        binding.tvBuyerPhone.text = userManager.getUserPhone()
        binding.tvPaymentMethod.text = order.paymentMethod
        binding.tvPaymentStatus.text = "Ödənilib"
        binding.tvPaymentStatus.setTextColor(
            requireContext().getColor(com.example.onemarket.R.color.cart_green)
        )

        // Cəmi
        binding.tvTotalAmount.text = String.format("%.2f ₼", order.totalAmount + 3.0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}