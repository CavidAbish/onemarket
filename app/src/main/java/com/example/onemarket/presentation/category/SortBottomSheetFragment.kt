package com.example.onemarket.presentation.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.onemarket.databinding.BottomSheetSortBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SortBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetSortBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val TAG = "SortBottomSheet"
        const val RESULT_KEY = "sort_selected"
        private const val ARG_CURRENT = "current_sort"

        fun newInstance(current: SortOption): SortBottomSheetFragment {
            return SortBottomSheetFragment().apply {
                arguments = Bundle().apply { putString(ARG_CURRENT, current.name) }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetSortBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val current = try {
            SortOption.valueOf(arguments?.getString(ARG_CURRENT) ?: SortOption.POPULAR.name)
        } catch (e: Exception) { SortOption.POPULAR }

        updateRadios(current)

        fun select(option: SortOption) {
            updateRadios(option)
            parentFragmentManager.setFragmentResult(
                RESULT_KEY,
                Bundle().apply { putString("sort_option", option.name) }
            )
            dismiss()
        }

        binding.optionPopular.setOnClickListener { select(SortOption.POPULAR) }
        binding.optionCheapest.setOnClickListener { select(SortOption.CHEAPEST) }
        binding.optionExpensive.setOnClickListener { select(SortOption.EXPENSIVE) }
        binding.optionBiggestDiscount.setOnClickListener { select(SortOption.BIGGEST_DISCOUNT) }
        binding.optionNewest.setOnClickListener { select(SortOption.NEWEST) }
        binding.btnClose.setOnClickListener { dismiss() }
    }

    private fun updateRadios(selected: SortOption) {
        binding.rbPopular.isChecked = selected == SortOption.POPULAR
        binding.rbCheapest.isChecked = selected == SortOption.CHEAPEST
        binding.rbExpensive.isChecked = selected == SortOption.EXPENSIVE
        binding.rbBiggestDiscount.isChecked = selected == SortOption.BIGGEST_DISCOUNT
        binding.rbNewest.isChecked = selected == SortOption.NEWEST
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
