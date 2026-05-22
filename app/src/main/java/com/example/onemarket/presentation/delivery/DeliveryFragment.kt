package com.example.onemarket.presentation.delivery

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.onemarket.R
import com.example.onemarket.data.local.CartManager
import com.example.onemarket.data.local.CityManager
import com.example.onemarket.data.local.PickupHistoryManager
import com.example.onemarket.databinding.FragmentDeliveryBinding
import com.example.onemarket.databinding.ItemOrderDeliveryBinding
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class DeliveryFragment : Fragment() {

    private var _binding: FragmentDeliveryBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var cartManager: CartManager
    @Inject lateinit var cityManager: CityManager
    @Inject lateinit var historyManager: PickupHistoryManager

    private val selectedAddresses = mutableMapOf<Int, String>()
    private val orderBindings = mutableListOf<ItemOrderDeliveryBinding>()
    private val pickupAddresses = mutableMapOf<Int, String>()

    // Persist time-slot date selection per order
    private val selectedSlotDates = mutableMapOf<Int, String>()

    // Track which order index triggered the map picker
    private var currentMapPickerOrderIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeliveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.tvSelectedCity.text = cityManager.getCity()
        binding.btnSelectCity.setOnClickListener {
            findNavController().navigate(
                DeliveryFragmentDirections.actionDeliveryFragmentToCityFragment()
            )
        }

        // Result from PickupMapFragment — update pickup address
        parentFragmentManager.setFragmentResultListener("pickup_result", viewLifecycleOwner) { _, bundle ->
            val address = bundle.getString("pickup_address", "")
            if (address.isNotEmpty()) {
                orderBindings.forEachIndexed { index, b ->
                    if (b.radioPickup.isChecked) {
                        pickupAddresses[index] = address
                        b.tvPickupAddress.text = address
                    }
                }
            }
        }

        // Result from PickupBottomSheetFragment — open pickup map
        parentFragmentManager.setFragmentResultListener("open_pickup_map", viewLifecycleOwner) { _, _ ->
            findNavController().navigate(
                DeliveryFragmentDirections.actionDeliveryFragmentToPickupMapFragment()
            )
        }

        // Result from MapPickerFragment — update courier address
        parentFragmentManager.setFragmentResultListener("map_result", viewLifecycleOwner) { _, bundle ->
            val address = bundle.getString("selected_address", "")
            if (address.isNotEmpty()) {
                val orderIndex = currentMapPickerOrderIndex
                selectedAddresses[orderIndex] = address
                historyManager.addAddress(address)
                val b = orderBindings.getOrNull(orderIndex) ?: return@setFragmentResultListener
                clearDeliveryOptions(b)
                b.radioCourier.isChecked = true
                b.tvCourierAddress.text = address
                b.tvCourierAddress.visibility = View.VISIBLE
                // Keep button visible with "dəyiş" text
                b.btnAddAddress.text = "Çatdırılma ünvanını dəyiş"
                b.btnAddAddress.visibility = View.VISIBLE
                showTimeSlots(b, orderIndex)
            }
        }

        // Result from AddressBottomSheetFragment — user selected a saved address
        parentFragmentManager.setFragmentResultListener("address_selected", viewLifecycleOwner) { _, bundle ->
            val address = bundle.getString("selected_address", "")
            val orderIndex = bundle.getInt("order_index", 0)
            if (address.isNotEmpty()) {
                selectedAddresses[orderIndex] = address
                val b = orderBindings.getOrNull(orderIndex) ?: return@setFragmentResultListener
                clearDeliveryOptions(b)
                b.radioCourier.isChecked = true
                b.tvCourierAddress.text = address
                b.tvCourierAddress.visibility = View.VISIBLE
                b.btnAddAddress.text = "Çatdırılma ünvanını dəyiş"
                b.btnAddAddress.visibility = View.VISIBLE
                showTimeSlots(b, orderIndex)
            }
        }

        // Result from AddressBottomSheetFragment — open map picker
        parentFragmentManager.setFragmentResultListener("open_address_map", viewLifecycleOwner) { _, bundle ->
            currentMapPickerOrderIndex = bundle.getInt("order_index", 0)
            findNavController().navigate(
                DeliveryFragmentDirections.actionDeliveryFragmentToMapPickerFragment()
            )
        }

        setupOrders()

        binding.btnNext.setOnClickListener {
            findNavController().navigate(
                DeliveryFragmentDirections.actionDeliveryFragmentToOrderSummaryFragment()
            )
        }
    }

    private fun setupOrders() {
        val cartItems = cartManager.getSelectedItems().ifEmpty { cartManager.getCartItems() }

        cartItems.forEachIndexed { index, item ->
            val itemBinding = ItemOrderDeliveryBinding.inflate(
                LayoutInflater.from(requireContext()),
                binding.ordersContainer,
                false
            )
            orderBindings.add(itemBinding)

            itemBinding.tvOrderNumber.text = "SİFARİŞ ${index + 1}"
            itemBinding.tvSellerName.text = item.product.brand.uppercase().ifEmpty { "ONEMARKET" }
            itemBinding.tvProductName.text = item.product.title
            itemBinding.tvPrice.text = "${item.product.price} ₼"
            itemBinding.tvOldPrice.text = "${item.product.originalPrice} ₼"
            itemBinding.tvOldPrice.paintFlags =
                itemBinding.tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            itemBinding.tvQuantity.text = "${item.quantity} əd"

            Glide.with(this).load(item.product.thumbnail).centerCrop().into(itemBinding.ivProductImage)

            // Default: "Özün götür" seçili
            clearDeliveryOptions(itemBinding)
            itemBinding.radioPickup.isChecked = true
            itemBinding.pickupAddressLayout.visibility = View.VISIBLE
            itemBinding.tvPickupAddress.text = "Bakı şəh. Nərimanov r., Möhsün Sənani küç., 153"
            itemBinding.tvPickupDate.text = getDateLabel(1)

            setupDeliveryOptions(itemBinding, index)
            binding.ordersContainer.addView(itemBinding.root)
        }
    }

    private fun getDateLabel(daysAfter: Int): String {
        val months = listOf("yan","fev","mar","apr","may","iyn","iyl","avq","sen","okt","noy","dek")
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, daysAfter)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val month = months[cal.get(Calendar.MONTH)]
        return if (daysAfter == 1) "Sabah, $day $month" else "$day $month"
    }

    private fun clearDeliveryOptions(b: ItemOrderDeliveryBinding) {
        b.radioCourier.isChecked = false
        b.radioPickup.isChecked = false
        b.btnAddAddress.visibility = View.GONE
        b.tvCourierAddress.visibility = View.GONE
        b.pickupAddressLayout.visibility = View.GONE
        b.optionPost.visibility = View.GONE
        hideTimeSlots(b)
    }

    private fun setupDeliveryOptions(b: ItemOrderDeliveryBinding, orderIndex: Int) {
        b.optionPost.visibility = View.GONE

        // Kuryerlə çatdırılma
        val courierClick = View.OnClickListener {
            clearDeliveryOptions(b)
            b.radioCourier.isChecked = true

            // Restore or set default date
            val savedDate = selectedSlotDates[orderIndex]
            b.tvCourierDate.text = if (savedDate != null) "$savedDate, " else "${getDateLabel(1)}, "

            val savedAddress = selectedAddresses[orderIndex]
            if (savedAddress != null) {
                b.tvCourierAddress.text = savedAddress
                b.tvCourierAddress.visibility = View.VISIBLE
                b.btnAddAddress.text = "Çatdırılma ünvanını dəyiş"
                b.btnAddAddress.visibility = View.VISIBLE
            } else {
                b.btnAddAddress.text = "Çatdırılma ünvanını əlavə et"
                b.btnAddAddress.visibility = View.VISIBLE
            }
            showTimeSlots(b, orderIndex)
        }
        b.optionCourier.setOnClickListener(courierClick)
        b.radioCourier.setOnClickListener(courierClick)

        // Özün götür
        val pickupClick = View.OnClickListener {
            clearDeliveryOptions(b)
            b.radioPickup.isChecked = true
            b.pickupAddressLayout.visibility = View.VISIBLE
            b.tvPickupAddress.text = pickupAddresses[orderIndex]
                ?: "Bakı şəh. Nərimanov r., Möhsün Sənani küç., 153"
        }
        b.optionPickup.setOnClickListener(pickupClick)
        b.radioPickup.setOnClickListener(pickupClick)

        // Ünvan əlavə et / dəyiş — show address bottom sheet
        b.btnAddAddress.setOnClickListener {
            val currentAddr = selectedAddresses[orderIndex] ?: ""
            AddressBottomSheetFragment.newInstance(orderIndex, currentAddr)
                .show(parentFragmentManager, AddressBottomSheetFragment.TAG)
        }

        // Pickup dəyiş
        b.btnChangePickup.setOnClickListener {
            val currentAddress = pickupAddresses[orderIndex] ?: b.tvPickupAddress.text.toString()
            PickupBottomSheetFragment.newInstance(currentAddress)
                .show(parentFragmentManager, PickupBottomSheetFragment.TAG)
        }
    }

    private fun hideTimeSlots(b: ItemOrderDeliveryBinding) {
        b.root.findViewWithTag<View>("timeSlotsContainer")?.visibility = View.GONE
    }

    private fun showTimeSlots(b: ItemOrderDeliveryBinding, orderIndex: Int) {
        val existing = b.root.findViewWithTag<View>("timeSlotsContainer")
        if (existing != null) {
            existing.visibility = View.VISIBLE
            return
        }

        val scrollView = HorizontalScrollView(requireContext()).apply {
            tag = "timeSlotsContainer"
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.topMargin = 8.dpToPx() }
            isHorizontalScrollBarEnabled = false
        }

        val container = android.widget.LinearLayout(requireContext()).apply {
            orientation = android.widget.LinearLayout.HORIZONTAL
            setPadding(12.dpToPx(), 0, 12.dpToPx(), 0)
        }

        val slots = generateTimeSlots()
        var selectedSlot: MaterialCardView? = null

        slots.forEach { slot ->
            val card = MaterialCardView(requireContext()).apply {
                val lp = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.marginEnd = 8.dpToPx() }
                layoutParams = lp
                radius = 8.dpToPx().toFloat()
                cardElevation = 0f
                setCardBackgroundColor(android.graphics.Color.WHITE)
                strokeWidth = 1.dpToPx()
                strokeColor = android.graphics.Color.parseColor("#E0E0E0")
            }

            val inner = android.widget.LinearLayout(requireContext()).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                gravity = android.view.Gravity.CENTER
                setPadding(12.dpToPx(), 10.dpToPx(), 12.dpToPx(), 10.dpToPx())
            }

            val tvTime = TextView(requireContext()).apply {
                text = slot.first
                textSize = 13f
                setTextColor(android.graphics.Color.parseColor("#1A1A1A"))
                gravity = android.view.Gravity.CENTER
            }

            val tvDate = TextView(requireContext()).apply {
                text = slot.second
                textSize = 12f
                setTextColor(android.graphics.Color.parseColor("#888888"))
                gravity = android.view.Gravity.CENTER
            }

            inner.addView(tvTime)
            inner.addView(tvDate)
            card.addView(inner)

            card.setOnClickListener {
                // Deselect previous
                selectedSlot?.setCardBackgroundColor(android.graphics.Color.WHITE)
                selectedSlot?.strokeColor = android.graphics.Color.parseColor("#E0E0E0")
                selectedSlot?.let { prev ->
                    (prev.getChildAt(0) as? android.widget.LinearLayout)?.let { ll ->
                        (ll.getChildAt(0) as? TextView)?.setTextColor(android.graphics.Color.parseColor("#1A1A1A"))
                        (ll.getChildAt(1) as? TextView)?.setTextColor(android.graphics.Color.parseColor("#888888"))
                    }
                }
                // Select this card
                card.strokeColor = android.graphics.Color.parseColor("#1A237E")
                selectedSlot = card

                // Save and display selected date
                selectedSlotDates[orderIndex] = slot.second
                b.tvCourierDate.text = "${slot.second}, "
            }

            container.addView(card)
        }

        scrollView.addView(container)

        try {
            val parent = b.optionCourier.parent as? android.widget.LinearLayout
            val idx = parent?.indexOfChild(b.optionCourier) ?: -1
            if (idx >= 0) parent?.addView(scrollView, idx + 1)
        } catch (e: Exception) {
            (b.root as? android.widget.LinearLayout)?.addView(scrollView)
        }
    }

    private fun generateTimeSlots(): List<Triple<String, String, Int>> {
        val slots = mutableListOf<Triple<String, String, Int>>()
        val months = listOf("yan","fev","mar","apr","may","iyn","iyl","avq","sen","okt","noy","dek")

        for (dayOffset in 1..5) {
            val dayCal = Calendar.getInstance()
            dayCal.add(Calendar.DAY_OF_YEAR, dayOffset)
            val day = dayCal.get(Calendar.DAY_OF_MONTH)
            val month = months[dayCal.get(Calendar.MONTH)]
            val dayLabel = if (dayOffset == 1) "Sabah" else "$day $month"

            slots.add(Triple("09:00-15:00", dayLabel, dayOffset))
            slots.add(Triple("15:00-21:00", dayLabel, dayOffset))
        }
        return slots
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()

    override fun onResume() {
        super.onResume()
        binding.tvSelectedCity.text = cityManager.getCity()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        orderBindings.clear()
        _binding = null
    }
}
