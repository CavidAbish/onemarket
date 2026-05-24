package com.example.onemarket.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onemarket.data.local.AddressManager
import com.example.onemarket.databinding.FragmentSavedAddressesBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SavedAddressesFragment : Fragment() {

    private var _binding: FragmentSavedAddressesBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var addressManager: AddressManager

    private lateinit var adapter: AddressAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedAddressesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        adapter = AddressAdapter { address ->
            findNavController().navigate(
                SavedAddressesFragmentDirections
                    .actionSavedAddressesFragmentToAddAddressFragment(addressId = address.id)
            )
        }

        binding.recyclerAddresses.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerAddresses.adapter = adapter

        binding.btnAddAddress.setOnClickListener {
            findNavController().navigate(
                SavedAddressesFragmentDirections
                    .actionSavedAddressesFragmentToAddAddressFragment(addressId = -1L)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        loadAddresses()
    }

    private fun loadAddresses() {
        val addresses = addressManager.getAddresses()
        adapter.submitList(addresses)

        if (addresses.isEmpty()) {
            binding.emptyLayout.visibility = View.VISIBLE
            binding.recyclerAddresses.visibility = View.GONE
            binding.tvSubtitle.text =
                "Ünvanları əlavə edin və sifarişlərin rəsmiləşdirilməsi prosesini " +
                "asanlaşdırmaq və tezləşdirmək üçün istifadə edin"
        } else {
            binding.emptyLayout.visibility = View.GONE
            binding.recyclerAddresses.visibility = View.VISIBLE
            binding.tvSubtitle.text =
                "Sifarişlərin çatdırılması üçün ünvanlarınızı yaddaşda saxlayın"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
