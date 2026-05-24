package com.example.onemarket.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.onemarket.data.local.Address
import com.example.onemarket.data.local.AddressManager
import com.example.onemarket.databinding.FragmentAddAddressBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddAddressFragment : Fragment() {

    private var _binding: FragmentAddAddressBinding? = null
    private val binding get() = _binding!!

    private val args: AddAddressFragmentArgs by navArgs()

    @Inject lateinit var addressManager: AddressManager

    private var selectedLat = 0.0
    private var selectedLng = 0.0
    private var existingAddress: Address? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        // Edit mode: load existing address
        val addressId = args.addressId
        if (addressId != -1L) {
            existingAddress = addressManager.getAddresses().find { it.id == addressId }
            existingAddress?.let { addr ->
                binding.etAddressName.setText(addr.name)
                binding.etAddressSearch.setText(addr.fullAddress)
                binding.etApartment.setText(addr.apartment)
                selectedLat = addr.lat
                selectedLng = addr.lng
                binding.cbDefault.isChecked = addr.isDefault
                binding.btnDelete.visibility = View.VISIBLE
            }
        }


        binding.btnPickFromMap.setOnClickListener {
            findNavController().navigate(
                AddAddressFragmentDirections.actionAddAddressFragmentToMapPickerFragment()
            )
        }


        parentFragmentManager.setFragmentResultListener(
            "map_result", viewLifecycleOwner
        ) { _, bundle ->
            val address = bundle.getString("selected_address", "")
            selectedLat = bundle.getDouble("lat", 0.0)
            selectedLng = bundle.getDouble("lng", 0.0)
            if (address.isNotEmpty()) {
                binding.etAddressSearch.setText(address)
            }
        }

        binding.btnSave.setOnClickListener { validateAndSave() }

        binding.btnDelete.setOnClickListener {
            existingAddress?.let { addr ->
                addressManager.deleteAddress(addr.id)
                Toast.makeText(requireContext(), "Ünvan silindi", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    private fun validateAndSave() {
        val name = binding.etAddressName.text?.toString()?.trim() ?: ""
        val fullAddress = binding.etAddressSearch.text?.toString()?.trim() ?: ""
        val apartment = binding.etApartment.text?.toString()?.trim() ?: ""
        val isDefault = binding.cbDefault.isChecked

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Ünvanın adını daxil edin", Toast.LENGTH_SHORT).show()
            binding.etAddressName.requestFocus()
            return
        }

        if (fullAddress.isEmpty()) {
            Toast.makeText(requireContext(), "Ünvanı daxil edin", Toast.LENGTH_SHORT).show()
            binding.etAddressSearch.requestFocus()
            return
        }

        val address = Address(
            id = existingAddress?.id ?: System.currentTimeMillis(),
            name = name,
            fullAddress = fullAddress,
            apartment = apartment,
            lat = selectedLat,
            lng = selectedLng,
            isDefault = isDefault
        )

        addressManager.saveAddress(address)
        Toast.makeText(requireContext(), "Ünvan yadda saxlanıldı", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
