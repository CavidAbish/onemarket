package com.example.onemarket.presentation.profile

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.onemarket.R
import com.example.onemarket.data.local.AddressManager
import com.example.onemarket.data.local.AppNotificationManager
import com.example.onemarket.data.local.CardManager
import com.example.onemarket.data.local.CartManager
import com.example.onemarket.data.local.CreditManager
import com.example.onemarket.data.local.FavoritesManager
import com.example.onemarket.data.local.OrderManager
import com.example.onemarket.data.local.PersonalInfoManager
import com.example.onemarket.data.local.PickupHistoryManager
import com.example.onemarket.data.local.RecentlyViewedManager
import com.example.onemarket.data.local.SearchManager
import com.example.onemarket.data.local.UserManager
import com.example.onemarket.databinding.FragmentDeleteAccountOtpBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DeleteAccountOtpFragment : Fragment() {

    private var _binding: FragmentDeleteAccountOtpBinding? = null
    private val binding get() = _binding!!

    private val args: DeleteAccountOtpFragmentArgs by navArgs()

    @Inject lateinit var userManager: UserManager
    @Inject lateinit var cardManager: CardManager
    @Inject lateinit var creditManager: CreditManager
    @Inject lateinit var notificationManager: AppNotificationManager
    @Inject lateinit var cartManager: CartManager
    @Inject lateinit var orderManager: OrderManager
    @Inject lateinit var favoritesManager: FavoritesManager
    @Inject lateinit var searchManager: SearchManager
    @Inject lateinit var recentlyViewedManager: RecentlyViewedManager
    @Inject lateinit var personalInfoManager: PersonalInfoManager
    @Inject lateinit var addressManager: AddressManager
    @Inject lateinit var pickupHistoryManager: PickupHistoryManager

    private var timer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeleteAccountOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val phone = args.phone
        binding.tvPhone.text = phone

        // OTP = son 4 rəqəm
        val digits = phone.filter { it.isDigit() }
        val correctOtp = digits.takeLast(4)

        setupOtpInputs(correctOtp)
        startTimer()

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.tvResend.setOnClickListener {
            binding.tvResend.visibility = View.GONE
            startTimer()
            Toast.makeText(requireContext(), "Kod yenidən göndərildi", Toast.LENGTH_SHORT).show()
        }

        // Klaviaturanı aç
        binding.otp1.requestFocus()
        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE)
                as InputMethodManager
        imm.showSoftInput(binding.otp1, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setupOtpInputs(correctOtp: String) {
        val fields = listOf(binding.otp1, binding.otp2, binding.otp3, binding.otp4)
        val lines  = listOf(binding.line1, binding.line2, binding.line3, binding.line4)

        setLineActive(lines[0], true)

        fields.forEachIndexed { index, field ->
            field.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1) {
                        setLineActive(lines[index], true)
                        if (index < fields.size - 1) {
                            fields[index + 1].requestFocus()
                            setLineActive(lines[index + 1], true)
                        } else {
                            val entered = fields.joinToString("") { it.text.toString() }
                            checkOtp(entered, correctOtp, fields, lines)
                        }
                    } else {
                        setLineActive(lines[index], false)
                    }
                }
            })

            field.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL
                    && event.action == KeyEvent.ACTION_DOWN
                    && field.text.isEmpty()
                    && index > 0
                ) {
                    fields[index - 1].requestFocus()
                    fields[index - 1].text?.clear()
                    setLineActive(lines[index], false)
                }
                false
            }
        }
    }

    private fun setLineActive(line: View, active: Boolean) {
        line.setBackgroundColor(
            if (active) Color.parseColor("#E91E8C") else Color.parseColor("#DDDDDD")
        )
    }

    private fun checkOtp(
        entered: String,
        correctOtp: String,
        fields: List<EditText>,
        lines: List<View>
    ) {
        if (entered == correctOtp) {
            val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE)
                    as InputMethodManager
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)

            // Hesaba aid bütün məlumatları sil
            cardManager.clearAll()
            creditManager.clearAll()
            notificationManager.clearAllNotifications()
            cartManager.clearAll()
            orderManager.clearAllOrders()
            favoritesManager.clearFavorites()
            searchManager.clearHistory()
            recentlyViewedManager.clearAll()
            personalInfoManager.clear()
            addressManager.clearAll()
            pickupHistoryManager.clearAll()

            // Hesabdan çıx
            userManager.logout()

            Toast.makeText(requireContext(), "Hesabınız silindi", Toast.LENGTH_SHORT).show()

            // Ana səhifəyə qayıt
            requireActivity()
                .findViewById<BottomNavigationView>(R.id.bottom_nav)
                .selectedItemId = R.id.homeFragment

        } else {
            Toast.makeText(requireContext(), "Yanlış kod, yenidən cəhd edin", Toast.LENGTH_SHORT).show()
            fields.forEach { it.text?.clear() }
            lines.forEach { it.setBackgroundColor(Color.parseColor("#DDDDDD")) }
            fields[0].requestFocus()
        }
    }

    private fun startTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(90_000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 60000
                val seconds = (millisUntilFinished % 60000) / 1000
                binding.tvTimer.text = String.format("%02d: %02d", minutes, seconds)
            }

            override fun onFinish() {
                binding.tvTimer.text = "00: 00"
                binding.tvResend.visibility = View.VISIBLE
            }
        }.start()
    }

    override fun onDestroyView() {
        timer?.cancel()
        super.onDestroyView()
        _binding = null
    }
}
