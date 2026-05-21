package com.example.onemarket.presentation.delivery

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.onemarket.data.local.CartManager
import com.example.onemarket.data.local.CityManager
import com.example.onemarket.databinding.FragmentDeliveryBinding
import com.example.onemarket.databinding.ItemOrderDeliveryBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class DeliveryFragment : Fragment() {

    private var _binding: FragmentDeliveryBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var cartManager: CartManager
    @Inject lateinit var cityManager: CityManager

    // Hər sifariş üçün seçilmiş ünvanı saxla
    private val selectedAddresses = mutableMapOf<Int, String>()

    // Hər sifariş üçün ItemOrderDeliveryBinding saxla
    private val orderBindings = mutableListOf<ItemOrderDeliveryBinding>()

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

        // Xəritədən gələn ünvanı qəbul et
        parentFragmentManager.setFragmentResultListener("map_result", viewLifecycleOwner) { _, bundle ->
            val address = bundle.getString("selected_address", "")
            val orderIndex = bundle.getInt("order_index", 0)
            if (address.isNotEmpty()) {
                selectedAddresses[orderIndex] = address

                // Həmin sifarişin binding-ini tap
                val b = orderBindings.getOrNull(orderIndex) ?: return@setFragmentResultListener

                // Kuryerlə çatdırılma seçili olsun
                clearDeliveryOptions(b)
                b.radioCourier.isChecked = true
                b.tvCourierAddress.text = address
                b.tvCourierAddress.visibility = View.VISIBLE
                b.btnAddAddress.visibility = View.GONE
            }
        }

        setupOrders()

        binding.btnNext.setOnClickListener {
            findNavController().navigate(
                DeliveryFragmentDirections.actionDeliveryFragmentToOrderSummaryFragment()
            )
        }
    }

    private fun setupOrders() {
        // Seçilmiş məhsulları göstər, yoxdursa bütün səbəti göstər
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

            Glide.with(this)
                .load(item.product.thumbnail)
                .centerCrop()
                .into(itemBinding.ivProductImage)

            // Dinamik tarixlər
            itemBinding.tvCourierDate.text = "${getDate(3)} may, "
            itemBinding.tvPostDate.text = "${getDate(4)} may, "
            itemBinding.tvPickupDate.text = "${getDate(3)} may, "

            setupDeliveryOptions(itemBinding, index)
            binding.ordersContainer.addView(itemBinding.root)
        }
    }

    private fun getDate(daysAfter: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, daysAfter)
        return cal.get(Calendar.DAY_OF_MONTH).toString()
    }

    private fun clearDeliveryOptions(b: ItemOrderDeliveryBinding) {
        b.radioCourier.isChecked = false
        b.radioPost.isChecked = false
        b.radioPickup.isChecked = false
        b.btnAddAddress.visibility = View.GONE
        b.tvCourierAddress.visibility = View.GONE
        b.pickupAddressLayout.visibility = View.GONE
    }

    private fun setupDeliveryOptions(b: ItemOrderDeliveryBinding, orderIndex: Int) {

        // Kuryerlə çatdırılma
        val courierClick = View.OnClickListener {
            clearDeliveryOptions(b)
            b.radioCourier.isChecked = true
            val savedAddress = selectedAddresses[orderIndex]
            if (savedAddress != null) {
                b.tvCourierAddress.text = savedAddress
                b.tvCourierAddress.visibility = View.VISIBLE
            } else {
                b.btnAddAddress.visibility = View.VISIBLE
            }
        }
        b.optionCourier.setOnClickListener(courierClick)
        b.radioCourier.setOnClickListener(courierClick)

        // Azərpoçt
        val postClick = View.OnClickListener {
            clearDeliveryOptions(b)
            b.radioPost.isChecked = true
        }
        b.optionPost.setOnClickListener(postClick)
        b.radioPost.setOnClickListener(postClick)

        // Özün götür
        val pickupClick = View.OnClickListener {
            clearDeliveryOptions(b)
            b.radioPickup.isChecked = true
            b.pickupAddressLayout.visibility = View.VISIBLE
            b.tvPickupAddress.text = "Bakı şəh. Nərimanov r., Möhsün Sənani küç., 153"
        }
        b.optionPickup.setOnClickListener(pickupClick)
        b.radioPickup.setOnClickListener(pickupClick)

        // Ünvan əlavə et — xəritə açılır
        b.btnAddAddress.setOnClickListener {
            val bundle = Bundle().apply { putInt("order_index", orderIndex) }
            parentFragmentManager.setFragmentResult("order_index", bundle)
            findNavController().navigate(
                DeliveryFragmentDirections.actionDeliveryFragmentToMapPickerFragment()
            )
        }

        b.btnChangePickup.setOnClickListener {
            Toast.makeText(requireContext(), "Məntəqəni dəyişdirin", Toast.LENGTH_SHORT).show()
        }
    }

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