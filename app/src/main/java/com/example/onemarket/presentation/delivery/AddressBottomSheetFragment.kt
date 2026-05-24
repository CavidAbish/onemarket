package com.example.onemarket.presentation.delivery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onemarket.R
import com.example.onemarket.data.local.AddressManager
import com.example.onemarket.data.local.PickupHistoryManager
import com.example.onemarket.databinding.BottomSheetAddressBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddressBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddressBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var historyManager: PickupHistoryManager
    @Inject lateinit var addressManager: AddressManager

    companion object {
        const val TAG = "AddressBottomSheet"
        private const val ARG_ORDER_INDEX = "order_index"
        private const val ARG_CURRENT_ADDRESS = "current_address"

        fun newInstance(orderIndex: Int, currentAddress: String = ""): AddressBottomSheetFragment {
            return AddressBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ORDER_INDEX, orderIndex)
                    putString(ARG_CURRENT_ADDRESS, currentAddress)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val orderIndex = arguments?.getInt(ARG_ORDER_INDEX, 0) ?: 0
        val currentAddress = arguments?.getString(ARG_CURRENT_ADDRESS, "") ?: ""

        val savedAddresses = addressManager.getAddresses()
        val history = historyManager.getAddressHistory()


        val savedFull = savedAddresses.map { it.fullAddress }
        val historyExtra = history.filter { it !in savedFull }
        val allAddresses = savedFull + historyExtra


        val preSelected = if (currentAddress.isNotEmpty()) currentAddress
                          else savedAddresses.find { it.isDefault }?.fullAddress ?: ""

        if (allAddresses.isNotEmpty()) {
            binding.tvAddressHistoryLabel.visibility = View.VISIBLE
            binding.rvAddressHistory.visibility = View.VISIBLE
            binding.tvAddressHistoryLabel.text = "Çatdırılma ünvanları"

            val adapter = AddressHistoryAdapter(allAddresses, preSelected) { address ->
                val bundle = Bundle().apply {
                    putString("selected_address", address)
                    putInt("order_index", orderIndex)
                }
                parentFragmentManager.setFragmentResult("address_selected", bundle)
                dismiss()
            }
            binding.rvAddressHistory.layoutManager = LinearLayoutManager(requireContext())
            binding.rvAddressHistory.adapter = adapter
        }

        binding.btnClose.setOnClickListener { dismiss() }

        binding.btnAddNewAddress.setOnClickListener {
            val bundle = Bundle().apply { putInt("order_index", orderIndex) }
            parentFragmentManager.setFragmentResult("open_address_map", bundle)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class AddressHistoryAdapter(
    private val items: List<String>,
    private val selectedAddress: String,
    private val onSelect: (String) -> Unit
) : RecyclerView.Adapter<AddressHistoryAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvAddress: TextView = view.findViewById(R.id.tvAddressItem)
        val rbAddress: RadioButton = view.findViewById(R.id.rbAddressItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_address_history, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val address = items[position]
        holder.tvAddress.text = address
        holder.rbAddress.isChecked = address == selectedAddress
        holder.itemView.setOnClickListener { onSelect(address) }
    }

    override fun getItemCount() = items.size
}
