package com.example.onemarket.presentation.delivery

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.onemarket.R
import com.example.onemarket.data.local.CartManager
import com.example.onemarket.databinding.FragmentPaymentBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    private val args: PaymentFragmentArgs by navArgs()
    private var isFormatting = false
    private var otpCode = ""
    private var timer: CountDownTimer? = null

    @Inject lateinit var cartManager: CartManager
    @Inject lateinit var orderManager: com.example.onemarket.data.local.OrderManager

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

            // Random 4 rəqəmli OTP yarat
            otpCode = (1000..9999).random().toString()

            // OTP-ni Toast ilə göstər (demo)
            Toast.makeText(requireContext(), "OTP kodunuz: $otpCode", Toast.LENGTH_LONG).show()

            // OTP dialog aç
            showOtpDialog()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigate(
                PaymentFragmentDirections.actionPaymentFragmentToPaymentFailedFragment()
            )
        }
    }

    private fun showOtpDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_otp, null)

        val otp1 = dialogView.findViewById<EditText>(R.id.dotp1)
        val otp2 = dialogView.findViewById<EditText>(R.id.dotp2)
        val otp3 = dialogView.findViewById<EditText>(R.id.dotp3)
        val otp4 = dialogView.findViewById<EditText>(R.id.dotp4)
        val line1 = dialogView.findViewById<View>(R.id.dline1)
        val line2 = dialogView.findViewById<View>(R.id.dline2)
        val line3 = dialogView.findViewById<View>(R.id.dline3)
        val line4 = dialogView.findViewById<View>(R.id.dline4)
        val tvTimer = dialogView.findViewById<TextView>(R.id.dtvTimer)
        val tvPhone = dialogView.findViewById<TextView>(R.id.dtvPhone)

        tvPhone.text = "Kart nömrənizə SMS göndərildi"

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.show()

        val fields = listOf(otp1, otp2, otp3, otp4)
        val lines = listOf(line1, line2, line3, line4)

        setupOtpFields(fields, lines) { entered ->
            if (entered == otpCode) {
                timer?.cancel()
                dialog.dismiss()
                // Sifarişləri saxla
                orderManager.addOrdersFromCart(cartManager.getCartItems())
                // Səbəti təmizlə
                cartManager.clearCart()
                Toast.makeText(requireContext(), "Ödəniş uğurlu oldu! 🎉", Toast.LENGTH_LONG).show()
                // Home-a keç
                requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
                    .selectedItemId = R.id.homeFragment
                findNavController().popBackStack(R.id.homeFragment, false)
            } else {
                Toast.makeText(requireContext(), "Yanlış kod, yenidən cəhd edin", Toast.LENGTH_SHORT).show()
                fields.forEach { it.text?.clear() }
                lines.forEach { it.setBackgroundColor(Color.parseColor("#DDDDDD")) }
                fields[0].requestFocus()
            }
        }

        // Geri sayım
        timer = object : CountDownTimer(90000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val m = millisUntilFinished / 60000
                val s = (millisUntilFinished % 60000) / 1000
                tvTimer.text = String.format("%02d:%02d", m, s)
            }
            override fun onFinish() {
                tvTimer.text = "00:00"
                dialog.dismiss()
            }
        }.start()

        otp1.requestFocus()
    }

    private fun setupOtpFields(
        fields: List<EditText>,
        lines: List<View>,
        onComplete: (String) -> Unit
    ) {
        fields.forEachIndexed { index, field ->
            field.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1) {
                        lines[index].setBackgroundColor(Color.parseColor("#E91E8C"))
                        if (index < fields.size - 1) {
                            fields[index + 1].requestFocus()
                        } else {
                            val entered = fields.joinToString("") { it.text.toString() }
                            onComplete(entered)
                        }
                    } else {
                        lines[index].setBackgroundColor(Color.parseColor("#DDDDDD"))
                    }
                }
            })

            field.setOnKeyListener { _, keyCode, event ->
                if (keyCode == android.view.KeyEvent.KEYCODE_DEL
                    && event.action == android.view.KeyEvent.ACTION_DOWN
                    && field.text.isEmpty() && index > 0
                ) {
                    fields[index - 1].requestFocus()
                    fields[index - 1].text?.clear()
                }
                false
            }
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
        timer?.cancel()
        super.onDestroyView()
        _binding = null
    }
}