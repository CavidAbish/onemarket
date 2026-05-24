package com.example.onemarket.presentation.category

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.onemarket.databinding.FragmentFilterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterFragment : Fragment() {

    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!

    private val args: FilterFragmentArgs by navArgs()

    private var selectedBrand: String = ""
    private var selectedSeller: String = ""
    private val sellerRadioButtons = mutableListOf<RadioButton>()
    private val sellerValues = mutableListOf<String>()

    companion object {
        const val RESULT_KEY = "filter_applied"
        const val CLEAR_KEY = "filter_cleared"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentFilter = args.filterState
        val availableBrands = args.availableBrands
            .split(",")
            .filter { it.isNotEmpty() }

        selectedBrand = currentFilter.brand
        selectedSeller = currentFilter.seller


        binding.tvCategoryValue.text = args.categoryName.ifEmpty { "Hamısı" }


        binding.tvProductCount.text = "Məhsulların tapılması: ${args.productCount}"


        if (currentFilter.minPrice > 0)
            binding.etMinPrice.setText(currentFilter.minPrice.toInt().toString())
        if (currentFilter.maxPrice > 0)
            binding.etMaxPrice.setText(currentFilter.maxPrice.toInt().toString())


        binding.switchDiscount.isChecked = currentFilter.discountOnly


        if (selectedBrand.isNotEmpty()) {
            binding.tvSelectedBrand.text = selectedBrand
            binding.tvSelectedBrand.visibility = View.VISIBLE
        }


        binding.rowBrand.setOnClickListener {
            if (availableBrands.isEmpty()) return@setOnClickListener
            val entries = listOf("") + availableBrands
            val labels = listOf("Hamısı") + availableBrands
            val checkedIndex = entries.indexOf(selectedBrand).coerceAtLeast(0)

            AlertDialog.Builder(requireContext())
                .setTitle("Brend seçin")
                .setSingleChoiceItems(labels.toTypedArray(), checkedIndex) { dialog, which ->
                    selectedBrand = entries[which]
                    if (selectedBrand.isEmpty()) {
                        binding.tvSelectedBrand.visibility = View.GONE
                    } else {
                        binding.tvSelectedBrand.text = selectedBrand
                        binding.tvSelectedBrand.visibility = View.VISIBLE
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Ləğv et", null)
                .show()
        }


        buildSellerList(availableBrands)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.btnClear.setOnClickListener {
            parentFragmentManager.setFragmentResult(CLEAR_KEY, Bundle())
            findNavController().popBackStack()
        }

        binding.btnApply.setOnClickListener {
            val minPrice = binding.etMinPrice.text?.toString()?.trim()?.toDoubleOrNull() ?: 0.0
            val maxPrice = binding.etMaxPrice.text?.toString()?.trim()?.toDoubleOrNull() ?: 0.0

            val newFilter = FilterState(
                sortBy = currentFilter.sortBy,
                minPrice = minPrice,
                maxPrice = maxPrice,
                brand = selectedBrand,
                discountOnly = binding.switchDiscount.isChecked,
                seller = selectedSeller
            )

            parentFragmentManager.setFragmentResult(
                RESULT_KEY,
                Bundle().apply { putParcelable("filter_state", newFilter) }
            )
            findNavController().popBackStack()
        }
    }

    private fun buildSellerList(brands: List<String>) {
        sellerRadioButtons.clear()
        sellerValues.clear()
        binding.llSellersContainer.removeAllViews()


        val entries = listOf("") + brands
        val labels = listOf("Bütün satıcılar") + brands

        entries.forEachIndexed { index, value ->
            val row = createSellerRow(labels[index], value == selectedSeller)
            val (layout, rb) = row
            sellerRadioButtons.add(rb)
            sellerValues.add(value)

            layout.setOnClickListener {
                sellerRadioButtons.forEach { it.isChecked = false }
                rb.isChecked = true
                selectedSeller = value
            }

            binding.llSellersContainer.addView(layout)


            val divider = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1
                ).also { it.marginStart = dp(16) }
                setBackgroundColor(Color.parseColor("#F0F0F0"))
            }
            binding.llSellersContainer.addView(divider)
        }
    }

    private fun createSellerRow(label: String, isChecked: Boolean): Pair<LinearLayout, RadioButton> {
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
            textSize = 14f
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
