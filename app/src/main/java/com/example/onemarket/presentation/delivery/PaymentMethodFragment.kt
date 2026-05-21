package com.example.onemarket.presentation.delivery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.onemarket.databinding.FragmentPaymentMethodBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentMethodFragment : Fragment() {

    private var _binding: FragmentPaymentMethodBinding? = null
    private val binding get() = _binding!!

    private val paymentCards get() = listOf(
        binding.cardPayOnline,
        binding.cardPayBirbank,
        binding.cardPayCredit,
        binding.cardPayOnDelivery
    )
    private val paymentRadios get() = listOf(
        binding.rbPayOnline,
        binding.rbPayBirbank,
        binding.rbPayCredit,
        binding.rbPayOnDelivery
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPaymentMethodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        paymentCards.forEachIndexed { index, card ->
            card.setOnClickListener { selectPayment(index) }
        }

        binding.btnApply.setOnClickListener {
            val selectedMethod = when {
                binding.rbPayBirbank.isChecked -> "BIRBANK"
                binding.rbPayCredit.isChecked -> "CREDIT"
                binding.rbPayOnDelivery.isChecked -> "DELIVERY"
                else -> "ONLINE"
            }
            val bundle = android.os.Bundle().apply {
                putString("selected_method", selectedMethod)
            }
            parentFragmentManager.setFragmentResult("paymentMethodResult", bundle)
            findNavController().popBackStack()
        }
    }

    private fun selectPayment(selectedIndex: Int) {
        paymentRadios.forEachIndexed { index, radio ->
            radio.isChecked = index == selectedIndex
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
