package com.example.onemarket.presentation.delivery

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
    private var isFormatting = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val amount = args.amount
        binding.tvPaymentAmount.text = String.format("%.2f AZN", amount)

        setupCardNumberFormat()
        setupExpiryFormat()

        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnPay.setOnClickListener {
            val cardNumber = binding.etCardNumber.text.toString().replace(" ", "")
            val expiry = binding.etExpiry.text.toString()
            val cvv = binding.etCvv.text.toString()

            if (cardNumber.length < 16) {
                Toast.makeText(requireContext(), "Kart nömrəsi 16 rəqəm olmalıdır", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (expiry.length < 5) {
                Toast.makeText(requireContext(), "AY/İL düzgün daxil edin (məs: 12/25)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (cvv.isEmpty()) {
                Toast.makeText(requireContext(), "CVV2 daxil edin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(requireContext(), "Ödəniş uğurla tamamlandı!", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack(com.example.onemarket.R.id.cartFragment, false)
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigate(
                PaymentFragmentDirections.actionPaymentFragmentToPaymentFailedFragment()
            )
        }
    }

    // Kart nömrəsi — hər 4 rəqəmdən sonra boşluq
    private fun setupCardNumberFormat() {
        binding.etCardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true

                val digits = s.toString().replace(" ", "")
                val formatted = StringBuilder()
                for (i in digits.indices) {
                    if (i > 0 && i % 4 == 0) formatted.append(" ")
                    formatted.append(digits[i])
                }

                binding.etCardNumber.setText(formatted.toString())
                binding.etCardNumber.setSelection(formatted.length)
                isFormatting = false
            }
        })
    }

    // AY/İL — 2 rəqəm yazıldıqda / əlavə olur, max 12/xx
    private fun setupExpiryFormat() {
        binding.etExpiry.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true

                var input = s.toString().replace("/", "")
                if (input.length > 4) input = input.take(4)

                val formatted = StringBuilder()
                for (i in input.indices) {
                    if (i == 2) formatted.append("/")
                    formatted.append(input[i])
                }

                // Ay max 12 yoxlaması
                if (formatted.length >= 2) {
                    val month = formatted.substring(0, 2).toIntOrNull() ?: 0
                    if (month > 12) {
                        isFormatting = false
                        binding.etExpiry.setText("12")
                        binding.etExpiry.setSelection(2)
                        return
                    }
                }

                binding.etExpiry.setText(formatted.toString())
                binding.etExpiry.setSelection(formatted.length)
                isFormatting = false
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}