package com.example.onemarket.presentation.delivery

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.onemarket.R
import com.example.onemarket.data.local.CartManager
import com.example.onemarket.data.local.OrderManager
import com.example.onemarket.data.local.UserManager
import com.example.onemarket.databinding.Fragment3dSecureBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ThreeDSecureFragment : Fragment() {

    private var _binding: Fragment3dSecureBinding? = null
    private val binding get() = _binding!!

    private val args: ThreeDSecureFragmentArgs by navArgs()
    private var otpCode = ""
    private var timer: CountDownTimer? = null

    @Inject lateinit var cartManager: CartManager
    @Inject lateinit var orderManager: OrderManager
    @Inject lateinit var userManager: UserManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = Fragment3dSecureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvAmount.text = String.format("%.2f AZN", args.amount)

        // Telefon nömrəsini maskala
        val phone = userManager.getUserPhone()
        binding.tvPhoneNumber.text = maskPhone(phone)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnClose.setOnClickListener { findNavController().popBackStack() }

        generateAndShowOtp()
        setupOtpFields()

        binding.tvDidntReceive.setOnClickListener {
            generateAndShowOtp()
        }
    }

    private fun maskPhone(phone: String): String {
        val digits = phone.filter { it.isDigit() }
        return if (digits.length >= 9) {
            val last4 = digits.takeLast(4)
            val first3 = digits.take(3)
            "$first3 ** *** ${last4.take(2)} ${last4.takeLast(2)}"
        } else phone
    }

    private fun generateAndShowOtp() {
        otpCode = (100000..999999).random().toString()

        // Toast yuxarıdan
        val toast = Toast.makeText(
            requireContext(),
            "OneMarket: Ödəniş üçün kodunuz: $otpCode — Kodu heç kimlə paylaşmayın!",
            Toast.LENGTH_LONG
        )
        toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 0)
        toast.show()

        startTimer()
    }

    private fun startTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val s = millisUntilFinished / 1000
                binding.tvResendTimer.text = "Yeni kod göndər: 0:${String.format("%02d", s)}"
                binding.tvResendTimer.setTextColor(Color.parseColor("#E91E8C"))
            }
            override fun onFinish() {
                binding.tvResendTimer.text = "Yeni kod göndərmək üçün toxunun"
                binding.tvResendTimer.setTextColor(Color.parseColor("#1565C0"))
            }
        }.start()
    }

    private fun setupOtpFields() {
        val fields = listOf(
            binding.dotp1, binding.dotp2, binding.dotp3,
            binding.dotp4, binding.dotp5, binding.dotp6
        )
        val lines = listOf(
            binding.dline1, binding.dline2, binding.dline3,
            binding.dline4, binding.dline5, binding.dline6
        )

        fields.forEachIndexed { index, field ->
            field.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1) {
                        lines[index].setBackgroundColor(Color.parseColor("#1A1A1A"))
                        if (index < fields.size - 1) {
                            fields[index + 1].requestFocus()
                        } else {
                            val entered = fields.joinToString("") { it.text.toString() }
                            verifyOtp(entered, fields, lines)
                        }
                    } else {
                        lines[index].setBackgroundColor(Color.parseColor("#CCCCCC"))
                    }
                }
            })
            field.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL
                    && event.action == KeyEvent.ACTION_DOWN
                    && field.text.isEmpty() && index > 0
                ) {
                    fields[index - 1].requestFocus()
                    fields[index - 1].text?.clear()
                }
                false
            }
        }
        fields[0].requestFocus()
    }

    private fun verifyOtp(entered: String, fields: List<EditText>, lines: List<View>) {
        if (entered == otpCode) {
            timer?.cancel()

            // Sifarişi saxla — ödəniş üsulunu installmentMonths-a görə müəyyən et
            val selectedItems = cartManager.getSelectedItems().ifEmpty { cartManager.getCartItems() }
            val paymentLabel = if (args.installmentMonths > 0)
                "Birbank taksit kartı ilə"
            else
                "Bank kartı vasitəsi ilə onlayn"
            orderManager.addOrdersFromCart(selectedItems, paymentMethod = paymentLabel)

            // Yalnız seçilmiş məhsulları sil, qalanlar qalsın
            cartManager.removeSelectedItems()

            Toast.makeText(requireContext(), "Ödəniş uğurlu oldu! 🎉", Toast.LENGTH_LONG).show()

            // Home-a keç — bottom nav vasitəsilə
            // Bu delivery→orderSummary→payment→3dSecure stack-ini avtomatik bağlayır
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
                .selectedItemId = R.id.homeFragment

        } else {
            Toast.makeText(requireContext(), "Yanlış kod, yenidən cəhd edin", Toast.LENGTH_SHORT).show()
            fields.forEach { it.text?.clear() }
            lines.forEach { it.setBackgroundColor(Color.parseColor("#CCCCCC")) }
            fields[0].requestFocus()
        }
    }

    override fun onDestroyView() {
        timer?.cancel()
        super.onDestroyView()
        _binding = null
    }
}