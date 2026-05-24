package com.example.onemarket.presentation.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.onemarket.databinding.BottomSheetPriceFilterBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PriceFilterBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetPriceFilterBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val TAG = "PriceFilterBottomSheet"
        const val RESULT_KEY = "price_filter_applied"
        private const val ARG_MIN = "min_price"
        private const val ARG_MAX = "max_price"

        fun newInstance(minPrice: Double, maxPrice: Double): PriceFilterBottomSheet {
            return PriceFilterBottomSheet().apply {
                arguments = Bundle().apply {
                    putDouble(ARG_MIN, minPrice)
                    putDouble(ARG_MAX, maxPrice)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetPriceFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val minPrice = arguments?.getDouble(ARG_MIN, 0.0) ?: 0.0
        val maxPrice = arguments?.getDouble(ARG_MAX, 0.0) ?: 0.0

        if (minPrice > 0) binding.etMinPrice.setText(minPrice.toInt().toString())
        if (maxPrice > 0) binding.etMaxPrice.setText(maxPrice.toInt().toString())

        binding.btnClose.setOnClickListener { dismiss() }

        binding.btnApply.setOnClickListener {
            val min = binding.etMinPrice.text?.toString()?.trim()?.toDoubleOrNull() ?: 0.0
            val max = binding.etMaxPrice.text?.toString()?.trim()?.toDoubleOrNull() ?: 0.0
            parentFragmentManager.setFragmentResult(
                RESULT_KEY,
                Bundle().apply {
                    putDouble("min_price", min)
                    putDouble("max_price", max)
                }
            )
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
