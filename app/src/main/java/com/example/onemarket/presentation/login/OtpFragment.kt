package com.example.onemarket.presentation.login

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.onemarket.data.local.UserManager
import com.example.onemarket.databinding.FragmentOtpBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OtpFragment : Fragment() {

    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!

    private val args: OtpFragmentArgs by navArgs()

    @Inject
    lateinit var userManager: UserManager

    private var timer: CountDownTimer? = null

    // Demo OTP kodu — real proyektdə Firebase/SMS API istifadə olunur
    private val demoOtp = "1234"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val phone = args.phone
        binding.tvPhone.text = phone

        setupOtpInputs()
        startTimer()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

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

    private fun setupOtpInputs() {
        val otpFields = listOf(binding.otp1, binding.otp2, binding.otp3, binding.otp4)

        // İlk xana aktiv
        binding.otp1.background =
            requireContext().getDrawable(com.example.onemarket.R.drawable.bg_otp_active)

        otpFields.forEachIndexed { index, field ->
            field.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1) {
                        // Növbəti xanaya keç
                        field.background = requireContext().getDrawable(
                            com.example.onemarket.R.drawable.bg_otp_active
                        )
                        if (index < otpFields.size - 1) {
                            otpFields[index + 1].requestFocus()
                            otpFields[index + 1].background = requireContext().getDrawable(
                                com.example.onemarket.R.drawable.bg_otp_active
                            )
                        } else {
                            // Son xana — kodu yoxla
                            checkOtp(otpFields)
                        }
                    } else {
                        field.background = requireContext().getDrawable(
                            com.example.onemarket.R.drawable.bg_otp_inactive
                        )
                    }
                }
            })

            // Backspace ilə əvvəlki xanaya keç
            field.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == android.view.KeyEvent.KEYCODE_DEL && field.text.isEmpty() && index > 0) {
                    otpFields[index - 1].requestFocus()
                    otpFields[index - 1].text?.clear()
                }
                false
            }
        }
    }

    private fun checkOtp(fields: List<EditText>) {
        val entered = fields.joinToString("") { it.text.toString() }

        // Demo: istənilən 4 rəqəmli kod qəbul edilir
        if (entered.length == 4) {
            userManager.saveUser("İstifadəçi", args.phone)
            Toast.makeText(requireContext(), "Xoş gəldiniz!", Toast.LENGTH_SHORT).show()

            // Klaviaturanı bağla
            val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE)
                    as InputMethodManager
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)

            // Geri qayıt (LoginFragment → ProfileFragment)
            findNavController().popBackStack(
                com.example.onemarket.R.id.profileFragment, false
            )
        }
    }

    private fun startTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(90000, 1000) {
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