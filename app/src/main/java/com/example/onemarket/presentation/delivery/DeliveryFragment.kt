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
import javax.inject.Inject

@AndroidEntryPoint
class DeliveryFragment : Fragment() {

    private var _binding: FragmentDeliveryBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var cartManager: CartManager
    @Inject lateinit var cityManager: CityManager

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

        // Xəritədən gələn ünvanı qəbul et
        parentFragmentManager.setFragmentResultListener("map_result", viewLifecycleOwner) { _, bundle ->
            val address = bundle.getString("selected_address", "")
            if (address.isNotEmpty()) {
                android.widget.Toast.makeText(requireContext(), "Ünvan: $address", android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        // Seçili şəhəri göstər
        binding.tvSelectedCity.text = cityManager.getCity()

        // Şəhər seçiminə keç
        binding.btnSelectCity.setOnClickListener {
            findNavController().navigate(
                DeliveryFragmentDirections.actionDeliveryFragmentToCityFragment()
            )
        }

        // Səbət məhsullarını sifariş kimi göstər
        val cartItems = cartManager.getCartItems()
        cartItems.forEachIndexed { index, item ->
            val itemBinding = ItemOrderDeliveryBinding.inflate(
                LayoutInflater.from(requireContext()),
                binding.ordersContainer,
                false
            )

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

            // Radio seçim məntiqi
            setupDeliveryOptions(itemBinding, index)

            binding.ordersContainer.addView(itemBinding.root)
        }

        binding.btnNext.setOnClickListener {
            Toast.makeText(requireContext(), "Sifariş rəsmiləşdirildi!", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private fun setupDeliveryOptions(b: ItemOrderDeliveryBinding, orderIndex: Int) {

        fun clearAll() {
            b.radioCourier.isChecked = false
            b.radioPost.isChecked = false
            b.radioPickup.isChecked = false
            b.btnAddAddress.visibility = View.GONE
            b.btnSelectPost.visibility = View.GONE
            b.btnSelectPickup.visibility = View.GONE
            b.pickupAddressLayout.visibility = View.GONE
        }

        // Kuryerlə çatdırılma
        b.optionCourier.setOnClickListener {
            clearAll()
            b.radioCourier.isChecked = true
            b.btnAddAddress.visibility = View.VISIBLE
        }
        b.radioCourier.setOnClickListener {
            clearAll()
            b.radioCourier.isChecked = true
            b.btnAddAddress.visibility = View.VISIBLE
        }

        // Azərpoçt
        b.optionPost.setOnClickListener {
            clearAll()
            b.radioPost.isChecked = true
            b.btnSelectPost.visibility = View.VISIBLE
        }
        b.radioPost.setOnClickListener {
            clearAll()
            b.radioPost.isChecked = true
            b.btnSelectPost.visibility = View.VISIBLE
        }

        // Özün götür
        b.optionPickup.setOnClickListener {
            clearAll()
            b.radioPickup.isChecked = true
            b.btnSelectPickup.visibility = View.VISIBLE
        }
        b.radioPickup.setOnClickListener {
            clearAll()
            b.radioPickup.isChecked = true
            b.btnSelectPickup.visibility = View.VISIBLE
        }

        // Ünvan əlavə et
        b.btnAddAddress.setOnClickListener {
            findNavController().navigate(
                DeliveryFragmentDirections.actionDeliveryFragmentToMapPickerFragment()
            )
        }

        // Azərpoçt məntəqəsi seç
        b.btnSelectPost.setOnClickListener {
            Toast.makeText(requireContext(), "Azərpoçt məntəqəsi seçin", Toast.LENGTH_SHORT).show()
        }

        // Götürülmə məntəqəsi seç
        b.btnSelectPickup.setOnClickListener {
            // Ünvanı göstər
            b.btnSelectPickup.visibility = View.GONE
            b.pickupAddressLayout.visibility = View.VISIBLE
            b.tvPickupAddress.text = "Bakı şəh. Nərimanov r., Möhsün Sənani küç., 153"
        }

        // Məntəqəni dəyişdir
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
        _binding = null
    }
}