package com.example.onemarket.presentation.delivery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.onemarket.databinding.FragmentPaymentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    private val args: PaymentFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val amount = args.amount
        binding.tvPaymentAmount.text = String.format("%.2f AZN", amount)

        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }

        // Ödəniş et
        binding.btnPay.setOnClickListener {
            val cardNumber = binding.etCardNumber.text.toString()
            val expiry = binding.etExpiry.text.toString()
            val cvv = binding.etCvv.text.toString()

            if (cardNumber.length < 16 || expiry.isEmpty() || cvv.length < 3) {
                Toast.makeText(requireContext(), "Kart məlumatlarını düzgün daxil edin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Demo — uğurlu ödəniş simulyasiyası
            Toast.makeText(requireContext(), "Ödəniş uğurla tamamlandı!", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack(com.example.onemarket.R.id.cartFragment, false)
        }

        // İmtina — ödəniş keçmədi ekranına keç
        binding.btnCancel.setOnClickListener {
            findNavController().navigate(
                PaymentFragmentDirections.actionPaymentFragmentToPaymentFailedFragment()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}