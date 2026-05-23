package com.example.onemarket.presentation.delivery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.onemarket.data.local.CartManager
import com.example.onemarket.data.local.CreditApplication
import com.example.onemarket.data.local.CreditManager
import com.example.onemarket.data.local.UserManager
import com.example.onemarket.databinding.FragmentOrderSummaryBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class OrderSummaryFragment : Fragment() {

    private var _binding: FragmentOrderSummaryBinding? = null
    private val binding get() = _binding!!

    private val args: OrderSummaryFragmentArgs by navArgs()

    @Inject lateinit var cartManager: CartManager
    @Inject lateinit var userManager: UserManager
    @Inject lateinit var orderManager: com.example.onemarket.data.local.OrderManager
    @Inject lateinit var creditManager: CreditManager
    @Inject lateinit var notificationManager: com.example.onemarket.data.local.AppNotificationManager

    private var selectedPaymentMethod = "ONLINE"
    private var totalAmount = 0.0
    private var originalAmountSum = 0.0
    private var discountSum = 0.0
    private var deliveryCostTotal = 0.0
    private var productCount = 0
    private var selectedMonths = 24

    companion object {
        private const val BIRBANK_MONTHS = 3
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrderSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        // Payment method card clicks
        binding.cardPayOnline.setOnClickListener { switchToOnline() }
        binding.cardPayCredit.setOnClickListener { switchToCredit() }

        binding.btnSelectPaymentMethod.setOnClickListener {
            findNavController().navigate(
                OrderSummaryFragmentDirections.actionOrderSummaryFragmentToPaymentMethodFragment(
                    currentMethod = selectedPaymentMethod
                )
            )
        }

        parentFragmentManager.setFragmentResultListener("paymentMethodResult", viewLifecycleOwner) { _, bundle ->
            val method = bundle.getString("selected_method", "ONLINE")
            applySelectedPaymentMethod(method)
        }

        // İstifadəçi məlumatları
        binding.tvContactName.text = userManager.getUserName().uppercase()
        binding.tvContactPhone.text = userManager.getUserPhone()

        // Cart items
        val cartItems = cartManager.getSelectedItems().ifEmpty { cartManager.getCartItems() }
        val deliveryCost = 3.0
        productCount = cartItems.sumOf { it.quantity }
        deliveryCostTotal = deliveryCost * cartItems.size

        cartItems.forEachIndexed { index, item ->
            val orderView = layoutInflater.inflate(
                com.example.onemarket.R.layout.item_order_summary,
                binding.ordersContainer, false
            )

            val tvOrderNum = orderView.findViewById<TextView>(com.example.onemarket.R.id.tvOrderNum)
            val tvProductName = orderView.findViewById<TextView>(com.example.onemarket.R.id.tvProductName)
            val tvOrderAmount = orderView.findViewById<TextView>(com.example.onemarket.R.id.tvOrderAmount)
            val tvDiscount = orderView.findViewById<TextView>(com.example.onemarket.R.id.tvDiscount)
            val tvDelivery = orderView.findViewById<TextView>(com.example.onemarket.R.id.tvDelivery)

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
            originalAmountSum += item.product.price * item.quantity
            discountSum += discount

            binding.ordersContainer.addView(orderView)
        }

        binding.tvTotalPayment.text = String.format("%.2f ₼", totalAmount)
        binding.btnPay.text = String.format("%.2f ₼ ödə", totalAmount)

        // Kredit xülasəsi doldur
        binding.tvCreditOrderLabel.text = "Sifarişin məbləği ($productCount məhsul):"
        binding.tvCreditOrderAmount.text = String.format("%.2f ₼", originalAmountSum)
        if (discountSum > 0) {
            binding.creditDiscountRow.visibility = View.VISIBLE
            binding.tvCreditDiscount.text = String.format("-%.2f ₼", discountSum)
        }
        binding.tvCreditDelivery.text = String.format("%.2f ₼", deliveryCostTotal)

        // Birbank kontakt məlumatları
        binding.tvBirbankContactName.text = userManager.getUserName().uppercase()
        binding.tvBirbankContactPhone.text = userManager.getUserPhone()

        // Birbank xülasəsi doldur
        binding.tvBirbankOrderLabel.text = "Sifarişin məbləği ($productCount məhsul):"
        binding.tvBirbankOrderAmount.text = String.format("%.2f ₼", originalAmountSum)
        if (discountSum > 0) {
            binding.birbankDiscountRow.visibility = View.VISIBLE
            binding.tvBirbankDiscount.text = String.format("-%.2f ₼", discountSum)
        }
        val birbankMonthly = if (totalAmount > 0) totalAmount / BIRBANK_MONTHS else 0.0
        binding.tvBirbankTaksit.text = "$BIRBANK_MONTHS ay ${String.format("%.2f ₼", birbankMonthly)}"
        binding.tvBirbankTotal.text = String.format("%.2f ₼", totalAmount)
        binding.tvBirbankDelivery.text = String.format("%.2f ₼", deliveryCostTotal)

        // Kredit formu ön doldurmaq
        val fullName = userManager.getUserName().trim()
        val nameParts = fullName.split(" ")
        if (nameParts.size >= 2) {
            binding.etCreditFirstName.setText(nameParts[0])
            binding.etCreditLastName.setText(nameParts.drop(1).joinToString(" "))
        } else {
            binding.etCreditFirstName.setText(fullName)
        }
        binding.etCreditPhone.setText(userManager.getUserPhone())

        // Ay seçim düymələri
        setupMonthButtons()

        // Kredit rejimi ilkin seçim
        if (args.isCredit) {
            selectPayment(1)
            selectedPaymentMethod = "CREDIT"
            showCreditUI(true)
        }

        binding.btnPay.setOnClickListener { onPayButtonClicked() }
    }

    private fun switchToOnline() {
        selectedPaymentMethod = "ONLINE"
        selectPayment(0)
        binding.iconPayOnline.visibility = View.VISIBLE
        binding.iconPayBirbank.visibility = View.GONE
        binding.iconPayCredit.visibility = View.GONE
        binding.iconPayDelivery.visibility = View.GONE
        binding.tvSelectedPayTitle.text = "Bank kartı vasitəsi ilə onlayn"
        binding.tvSelectedPaySubtitle.text = "Onlayn ödəmək"
        binding.tvSelectedPaySubtitle.visibility = View.VISIBLE
        showNormalPaymentUI()
        binding.btnPay.text = String.format("%.2f ₼ ödə", totalAmount)
    }

    private fun switchToCredit() {
        selectedPaymentMethod = "CREDIT"
        selectPayment(1)
        showCreditUI(true)
    }

    private fun setupMonthButtons() {
        val monthButtons = listOf(
            binding.btnMonth2 to 2,
            binding.btnMonth3 to 3,
            binding.btnMonth6 to 6,
            binding.btnMonth12 to 12,
            binding.btnMonth18 to 18,
            binding.btnMonth24 to 24
        )

        updateMonthButtonsUI(monthButtons, selectedMonths)
        updateMonthlyPayment()

        monthButtons.forEach { (btn, months) ->
            btn.setOnClickListener {
                selectedMonths = months
                updateMonthButtonsUI(monthButtons, selectedMonths)
                updateMonthlyPayment()
            }
        }
    }

    private fun updateMonthButtonsUI(buttons: List<Pair<TextView, Int>>, selected: Int) {
        buttons.forEach { (btn, months) ->
            if (months == selected) {
                btn.setBackgroundResource(com.example.onemarket.R.drawable.bg_month_button_selected)
                btn.setTextColor(android.graphics.Color.WHITE)
            } else {
                btn.setBackgroundResource(com.example.onemarket.R.drawable.bg_month_button)
                btn.setTextColor(android.graphics.Color.parseColor("#1A1A1A"))
            }
        }
    }

    private fun updateMonthlyPayment() {
        val monthly = if (selectedMonths > 0 && totalAmount > 0) totalAmount / selectedMonths else 0.0
        binding.tvMonthlyPayment.text = String.format("%.2f ₼", monthly)
        binding.tvCreditMonthlySummary.text = String.format("%.2f ₼ (%d ay)", monthly, selectedMonths)
    }

    private fun showNormalPaymentUI() {
        binding.creditContainer.visibility = View.GONE
        binding.birbankSummaryContainer.visibility = View.GONE
        binding.tvContactHeader.visibility = View.VISIBLE
        binding.cardContact.visibility = View.VISIBLE
        binding.ordersContainer.visibility = View.VISIBLE
        binding.totalRow.visibility = View.VISIBLE
        binding.tvLegal.visibility = View.VISIBLE
        binding.tvCreditLegal.visibility = View.GONE
        binding.btnSelectPaymentMethod.visibility = View.VISIBLE
    }

    private fun showCreditUI(show: Boolean) {
        if (show) {
            binding.creditContainer.visibility = View.VISIBLE
            binding.birbankSummaryContainer.visibility = View.GONE
            binding.tvContactHeader.visibility = View.GONE
            binding.cardContact.visibility = View.GONE
            binding.ordersContainer.visibility = View.GONE
            binding.totalRow.visibility = View.GONE
            binding.tvLegal.visibility = View.GONE
            binding.tvCreditLegal.visibility = View.VISIBLE
            binding.btnSelectPaymentMethod.visibility = View.VISIBLE
            binding.btnPay.text = "Kredit üçün müraciət et"
        } else {
            showNormalPaymentUI()
            binding.btnPay.text = String.format("%.2f ₼ ödə", totalAmount)
        }
    }

    private fun showBirbankUI(show: Boolean) {
        if (show) {
            binding.birbankSummaryContainer.visibility = View.VISIBLE
            binding.creditContainer.visibility = View.GONE
            binding.tvContactHeader.visibility = View.GONE
            binding.cardContact.visibility = View.GONE
            binding.ordersContainer.visibility = View.GONE
            binding.totalRow.visibility = View.GONE
            binding.tvLegal.visibility = View.GONE
            binding.tvCreditLegal.visibility = View.GONE
            binding.btnSelectPaymentMethod.visibility = View.VISIBLE
            binding.btnPay.text = "Təsdiqlə və ödənişə davam et"
        } else {
            showNormalPaymentUI()
            binding.btnPay.text = String.format("%.2f ₼ ödə", totalAmount)
        }
    }

    private fun onPayButtonClicked() {
        if (selectedPaymentMethod == "CREDIT") {
            if (!binding.cbConsent.isChecked) {
                Toast.makeText(requireContext(), "Zəhmət olmasa razılığınızı bildirin", Toast.LENGTH_SHORT).show()
                return
            }
            val cartItems = cartManager.getSelectedItems().ifEmpty { cartManager.getCartItems() }
            val productNames = cartItems.joinToString(", ") { it.product.title }
            val monthly = if (selectedMonths > 0) totalAmount / selectedMonths else 0.0
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val application = CreditApplication(
                id = System.currentTimeMillis().toInt(),
                productNames = productNames,
                totalAmount = totalAmount,
                monthlyPayment = monthly,
                months = selectedMonths,
                date = sdf.format(Date())
            )
            creditManager.addApplication(application)
            cartManager.removeSelectedItems()

            // Kredit bildirişi
            val now = java.util.Date()
            val timeFmt = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            val dateFmt = java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault())
            notificationManager.addNotification(
                com.example.onemarket.data.local.AppNotification(
                    id = (System.currentTimeMillis() % 1_000_000_000L).toInt(),
                    orderId = 0,
                    title = "Kredit müraciəti göndərildi",
                    body = "Kredit müraciətiniz qəbul edildi. Yaxında sizinlə əlaqə saxlanılacaq",
                    type = "credit",
                    dateTime = "${timeFmt.format(now)} ${dateFmt.format(now)}"
                )
            )

            AlertDialog.Builder(requireContext())
                .setTitle("Uğurlu")
                .setMessage("Kredit müraciətiniz uğurla göndərilmişdir")
                .setPositiveButton("Tamam") { dialog, _ ->
                    dialog.dismiss()
                    findNavController().navigate(
                        OrderSummaryFragmentDirections.actionOrderSummaryFragmentToCreditApplicationsFragment()
                    )
                }
                .setCancelable(false)
                .show()
        } else {
            val selectedItems = cartManager.getSelectedItems().ifEmpty { cartManager.getCartItems() }
            val paymentLabel = when (selectedPaymentMethod) {
                "DELIVERY" -> "Təhvil alarkən bank kartı vasitəsi ilə"
                "BIRBANK"  -> "Birbank taksit kartı ilə"
                else       -> "Bank kartı vasitəsi ilə onlayn"
            }
            if (selectedPaymentMethod == "DELIVERY") {
                orderManager.addOrdersFromCart(selectedItems, paymentLabel, deliveryAddress = "Kuryerlə çatdırılma")
                cartManager.removeSelectedItems()
                requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                    com.example.onemarket.R.id.bottom_nav
                ).selectedItemId = com.example.onemarket.R.id.homeFragment
            } else if (selectedPaymentMethod == "BIRBANK") {
                findNavController().navigate(
                    OrderSummaryFragmentDirections.actionOrderSummaryFragmentToPaymentFragment(
                        amount = totalAmount.toFloat(),
                        installmentMonths = BIRBANK_MONTHS
                    )
                )
            } else {
                findNavController().navigate(
                    OrderSummaryFragmentDirections.actionOrderSummaryFragmentToPaymentFragment(
                        amount = totalAmount.toFloat(),
                        installmentMonths = 0
                    )
                )
            }
        }
    }

    private fun applySelectedPaymentMethod(method: String) {
        selectedPaymentMethod = method

        binding.iconPayOnline.visibility = View.GONE
        binding.iconPayBirbank.visibility = View.GONE
        binding.iconPayCredit.visibility = View.GONE
        binding.iconPayDelivery.visibility = View.GONE

        when (method) {
            "ONLINE" -> {
                selectPayment(0)
                binding.iconPayOnline.visibility = View.VISIBLE
                binding.tvSelectedPayTitle.text = "Bank kartı vasitəsi ilə onlayn"
                binding.tvSelectedPaySubtitle.text = "Onlayn ödəmək"
                binding.tvSelectedPaySubtitle.visibility = View.VISIBLE
                showNormalPaymentUI()
                binding.btnPay.text = String.format("%.2f ₼ ödə", totalAmount)
            }
            "BIRBANK" -> {
                selectPayment(0)
                binding.iconPayBirbank.visibility = View.VISIBLE
                val birbankMonthly = if (totalAmount > 0) totalAmount / BIRBANK_MONTHS else 0.0
                binding.tvSelectedPayTitle.text = "Birbank taksit kartı ilə"
                binding.tvSelectedPaySubtitle.text = "$BIRBANK_MONTHS ay ${String.format("%.2f", birbankMonthly)} ₼"
                binding.tvSelectedPaySubtitle.visibility = View.VISIBLE
                showBirbankUI(true)
            }
            "CREDIT" -> {
                // Don't change cardPayOnline - keep it as is
                selectPayment(1)
                showCreditUI(true)
            }
            "DELIVERY" -> {
                selectPayment(0)
                binding.iconPayDelivery.visibility = View.VISIBLE
                binding.tvSelectedPayTitle.text = "Təhvil alarkən bank kartı vasitəsi ilə"
                binding.tvSelectedPaySubtitle.text = "Təhvil alarkən ödəmək"
                binding.tvSelectedPaySubtitle.visibility = View.VISIBLE
                showNormalPaymentUI()
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
