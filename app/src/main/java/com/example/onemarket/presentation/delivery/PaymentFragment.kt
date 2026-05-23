package com.example.onemarket.presentation.delivery

import android.content.Context
import android.content.SharedPreferences
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

    private val cardPrefs: SharedPreferences by lazy {
        // Birbank taksit üçün ayrı saxlama — online kartla qarışmasın
        val prefsKey = if (args.installmentMonths > 0) "saved_card_birbank" else "saved_card"
        requireContext().getSharedPreferences(prefsKey, Context.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val amount = args.amount
        val installmentMonths = args.installmentMonths
        binding.tvPaymentAmount.text = String.format("%.2f AZN", amount)

        // Birbank taksit - əlavə detallar
        if (installmentMonths > 0) {
            val monthly = amount / installmentMonths
            val orderId = (System.currentTimeMillis() % 100000000L).toString()
            binding.tvInstallmentSubtitle.text =
                "Ödənilcək sifariş № $orderId, hissə-hissə ödəniş: $installmentMonths ay"
            binding.tvInstallmentSubtitle.visibility = View.VISIBLE
            binding.installmentDetailRow.visibility = View.VISIBLE
            binding.tvInstallmentDetail.text =
                "${String.format("%.2f", monthly)} AZN × $installmentMonths ay"
            binding.tvPaymentIdValue.text = orderId
        }

        setupCardNumberFormat()
        setupExpiryFormat()
        loadSavedCard()

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

            // Kartı yadda saxla
            if (binding.cbSaveCard.isChecked) {
                saveCard(
                    cardNumber = binding.etCardNumber.text.toString(),
                    expiry = expiry,
                    cvv = cvv
                )
            }

            // 3D Secure ekranına keç — installmentMonths ötürülür ki,
            // ThreeDSecureFragment doğru paymentMethod ilə sifarişi saxlasın
            findNavController().navigate(
                PaymentFragmentDirections.actionPaymentFragmentToThreeDSecureFragment(
                    amount = amount,
                    installmentMonths = installmentMonths
                )
            )
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigate(
                PaymentFragmentDirections.actionPaymentFragmentToPaymentFailedFragment()
            )
        }
    }

    private fun saveCard(cardNumber: String, expiry: String, cvv: String) {
        cardPrefs.edit()
            .putString("card_number", cardNumber)
            .putString("expiry", expiry)
            .putString("cvv", cvv)
            .apply()
        Toast.makeText(requireContext(), "Kart məlumatları saxlanıldı", Toast.LENGTH_SHORT).show()
    }

    private fun loadSavedCard() {
        val savedNumber = cardPrefs.getString("card_number", null)
        val savedExpiry = cardPrefs.getString("expiry", null)
        val savedCvv = cardPrefs.getString("cvv", null)

        if (!savedNumber.isNullOrEmpty()) {
            binding.etCardNumber.setText(savedNumber)
            binding.etExpiry.setText(savedExpiry)
            binding.etCvv.setText(savedCvv)
            binding.cbSaveCard.isChecked = true
        }
    }

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