package com.example.onemarket.presentation.category

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import com.example.onemarket.databinding.BottomSheetBrandFilterBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BrandFilterBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetBrandFilterBinding? = null
    private val binding get() = _binding!!

    private var selectedBrand: String = ""
    private val radioButtons = mutableListOf<RadioButton>()
    private val brandValues = mutableListOf<String>()

    companion object {
        const val TAG = "BrandFilterBottomSheet"
        const val RESULT_KEY = "brand_filter_applied"
        private const val ARG_BRANDS = "brands"
        private const val ARG_SELECTED = "selected_brand"

        fun newInstance(brands: List<String>, selectedBrand: String): BrandFilterBottomSheet {
            return BrandFilterBottomSheet().apply {
                arguments = Bundle().apply {
                    putStringArrayList(ARG_BRANDS, ArrayList(brands))
                    putString(ARG_SELECTED, selectedBrand)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetBrandFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val brands = arguments?.getStringArrayList(ARG_BRANDS) ?: arrayListOf()
        selectedBrand = arguments?.getString(ARG_SELECTED) ?: ""

        buildBrandList(brands)

        binding.btnClose.setOnClickListener { dismiss() }

        binding.btnApply.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                RESULT_KEY,
                Bundle().apply { putString("brand", selectedBrand) }
            )
            dismiss()
        }
    }

    private fun buildBrandList(brands: List<String>) {
        radioButtons.clear()
        brandValues.clear()
        binding.llBrandsContainer.removeAllViews()

        // "Hamısı" = empty string, then each brand
        val entries = listOf("") + brands
        val labels = listOf("Hamısı") + brands

        entries.forEachIndexed { index, value ->
            val isChecked = value == selectedBrand
            val (row, rb) = createRow(labels[index], isChecked)

            radioButtons.add(rb)
            brandValues.add(value)

            row.setOnClickListener {
                radioButtons.forEach { it.isChecked = false }
                rb.isChecked = true
                selectedBrand = value
            }

            binding.llBrandsContainer.addView(row)

            // Divider
            val divider = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1
                ).also { it.marginStart = dp(16) }
                setBackgroundColor(Color.parseColor("#F0F0F0"))
            }
            binding.llBrandsContainer.addView(divider)
        }
    }

    private fun createRow(label: String, isChecked: Boolean): Pair<LinearLayout, RadioButton> {
        val row = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), 0, dp(16), 0)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(52)
            )
            val tv = TypedValue()
            requireContext().theme.resolveAttribute(android.R.attr.selectableItemBackground, tv, true)
            setBackgroundResource(tv.resourceId)
            isClickable = true
            isFocusable = true
        }

        val rb = RadioButton(requireContext()).apply {
            this.isChecked = isChecked
            isClickable = false
            isFocusable = false
            buttonTintList = android.content.res.ColorStateList.valueOf(
                Color.parseColor("#E91E8C")
            )
        }

        val tv = TextView(requireContext()).apply {
            text = label
            textSize = 15f
            setTextColor(Color.parseColor("#1A1A1A"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                .also { it.marginStart = dp(12) }
        }

        row.addView(rb)
        row.addView(tv)
        return Pair(row, rb)
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
