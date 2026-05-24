package com.example.onemarket.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.onemarket.data.local.CreditManager
import com.example.onemarket.databinding.FragmentCreditDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreditDetailFragment : Fragment() {

    private var _binding: FragmentCreditDetailBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var creditManager: CreditManager

    private val args: CreditDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreditDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        val app = creditManager.getApplications().find { it.id == args.applicationId }
            ?: run { findNavController().popBackStack(); return }

        binding.tvTitle.text = "Kredit müraciəti №${app.id}"
        binding.tvCreditTitle.text = "Kredit müraciəti №${app.id}"
        binding.tvCreditDate.text = app.date
        binding.tvProductNames.text = app.productNames
        binding.tvTotalAmount.text = String.format("%.2f ₼", app.totalAmount)
        binding.tvMonthly.text =
            String.format("Aylıq: %.2f ₼ × %d ay", app.monthlyPayment, app.months)
        binding.tvPaymentMethod.text = "Kredit ilə ödəniş"
        binding.tvPaymentStatus.text = "Kredit müraciəti gözlənilir"
        binding.tvTotal.text = String.format("%.2f ₼", app.totalAmount)

        // Məhsul şəkli (productImageUrl may be runtime-null for entries saved before this field existed)
        val imageUrl = app.productImageUrl ?: ""
        if (imageUrl.isNotEmpty()) {
            binding.ivProductImage.visibility = View.VISIBLE
            Glide.with(this)
                .load(imageUrl)
                .centerCrop()
                .into(binding.ivProductImage)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
