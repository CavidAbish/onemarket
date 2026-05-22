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
import com.example.onemarket.data.local.PickupHistoryManager
import com.example.onemarket.data.local.PickupPoint
import com.example.onemarket.databinding.BottomSheetPickupBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PickupBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetPickupBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var historyManager: PickupHistoryManager

    private var currentAddress: String = ""

    companion object {
        const val TAG = "PickupBottomSheet"
        private const val ARG_ADDRESS = "address"

        fun newInstance(currentAddress: String): PickupBottomSheetFragment {
            return PickupBottomSheetFragment().apply {
                arguments = Bundle().apply { putString(ARG_ADDRESS, currentAddress) }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetPickupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentAddress = arguments?.getString(ARG_ADDRESS) ?: ""

        val history = historyManager.getPickupHistory()
        if (history.isNotEmpty()) {
            binding.tvHistoryLabel.visibility = View.VISIBLE
            binding.rvPickupHistory.visibility = View.VISIBLE

            val adapter = PickupHistoryAdapter(history, currentAddress) { point ->
                val bundle = Bundle().apply {
                    putString("pickup_address", point.address)
                    putString("pickup_name", point.name)
                    putInt("pickup_id", point.id)
                }
                parentFragmentManager.setFragmentResult("pickup_result", bundle)
                dismiss()
            }
            binding.rvPickupHistory.layoutManager = LinearLayoutManager(requireContext())
            binding.rvPickupHistory.adapter = adapter
        }

        binding.btnClose.setOnClickListener { dismiss() }

        binding.btnSelectPickup.setOnClickListener {
            parentFragmentManager.setFragmentResult("open_pickup_map", Bundle())
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class PickupHistoryAdapter(
    private val items: List<PickupPoint>,
    private val selectedAddress: String,
    private val onSelect: (PickupPoint) -> Unit
) : RecyclerView.Adapter<PickupHistoryAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvHistoryPointName)
        val tvAddress: TextView = view.findViewById(R.id.tvHistoryPointAddress)
        val rbPoint: RadioButton = view.findViewById(R.id.rbHistoryPoint)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_pickup_history, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.tvName.text = item.name
        holder.tvAddress.text = item.address
        holder.rbPoint.isChecked = item.address == selectedAddress
        holder.itemView.setOnClickListener { onSelect(item) }
    }

    override fun getItemCount() = items.size
}
