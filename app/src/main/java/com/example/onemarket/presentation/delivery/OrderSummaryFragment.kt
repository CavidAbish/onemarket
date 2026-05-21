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
    @Inject lateinit var orderManager: com.example.onemarket.data.local.OrderManager

    private var selectedPaymentMethod = "ONLINE"
    private var totalAmount = 0.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrderSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.cardPayOnline.setOnClickListener { selectPayment(0) }
        binding.cardPayCredit.setOnClickListener { selectPayment(1) }

        binding.btnSelectPaymentMethod.setOnClickListener {
            findNavController().navigate(
                OrderSummaryFragmentDirections.actionOrderSummaryFragmentToPaymentMethodFragment()
            )
        }

        parentFragmentManager.setFragmentResultListener("paymentMethodResult", viewLifecycleOwner) { _, bundle ->
            val method = bundle.getString("selected_method", "ONLINE")
            applySelectedPaymentMethod(method)
        }

        // İstifadəçi məlumatları
        binding.tvContactName.text = userManager.getUserName().uppercase()
        binding.tvContactPhone.text = userManager.getUserPhone()

        // Səbət məhsulları
        val cartItems = cartManager.getCartItems()
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

        this.totalAmount = totalAmount
        binding.tvTotalPayment.text = String.format("%.2f ₼", totalAmount)
        binding.btnPay.text = String.format("%.2f ₼ ödə", totalAmount)

        binding.btnPay.setOnClickListener { onPayButtonClicked() }
    }

    private fun onPayButtonClicked() {
        if (selectedPaymentMethod == "DELIVERY") {
            orderManager.addOrdersFromCart(cartManager.getCartItems())
            cartManager.clearCart()
            findNavController().navigate(
                OrderSummaryFragmentDirections.actionOrderSummaryFragmentToMyOrdersFragment()
            )
        } else {
            findNavController().navigate(
                OrderSummaryFragmentDirections.actionOrderSummaryFragmentToPaymentFragment(totalAmount.toFloat())
            )
        }
    }

    private fun applySelectedPaymentMethod(method: String) {
        selectedPaymentMethod = method
        selectPayment(0)

        binding.iconPayOnline.visibility = View.GONE
        binding.iconPayBirbank.visibility = View.GONE
        binding.iconPayCredit.visibility = View.GONE
        binding.iconPayDelivery.visibility = View.GONE

        when (method) {
            "ONLINE" -> {
                binding.iconPayOnline.visibility = View.VISIBLE
                binding.tvSelectedPayTitle.text = "Bank kartı vasitəsi ilə onlayn"
                binding.tvSelectedPaySubtitle.text = "Onlayn ödəmək"
                binding.tvSelectedPaySubtitle.visibility = View.VISIBLE
                binding.btnPay.text = String.format("%.2f ₼ ödə", totalAmount)
            }
            "BIRBANK" -> {
                binding.iconPayBirbank.visibility = View.VISIBLE
                binding.tvSelectedPayTitle.text = "Birbank taksit kartı ilə"
                binding.tvSelectedPaySubtitle.text = "18 ay 0.33 ₼"
                binding.tvSelectedPaySubtitle.visibility = View.VISIBLE
                binding.btnPay.text = String.format("%.2f ₼ ödə", totalAmount)
            }
            "CREDIT" -> {
                binding.iconPayCredit.visibility = View.VISIBLE
                binding.tvSelectedPayTitle.text = "Kredit"
                binding.tvSelectedPaySubtitle.visibility = View.GONE
                binding.btnPay.text = String.format("%.2f ₼ ödə", totalAmount)
            }
            "DELIVERY" -> {
                binding.iconPayDelivery.visibility = View.VISIBLE
                binding.tvSelectedPayTitle.text = "Təhvil alarkən bank kartı vasitəsi ilə"
                binding.tvSelectedPaySubtitle.text = "Təhvil alarkən ödəmək"
                binding.tvSelectedPaySubtitle.visibility = View.VISIBLE
                binding.btnPay.text = "Təsdiq edirəm"
            }
        }
    }

    private fun selectPayment(selectedIndex: Int) {
        binding.rbPayOnline.isChecked = selectedIndex == 0
        binding.rbPayCredit.isChecked = selectedIndex == 1
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}