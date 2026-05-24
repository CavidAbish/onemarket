package com.example.onemarket.presentation.profile

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.onemarket.R
import com.example.onemarket.data.local.CardManager
import com.example.onemarket.data.local.CardModel
import com.example.onemarket.databinding.FragmentAddCardBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddCardFragment : Fragment() {

    private var _binding: FragmentAddCardBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var cardManager: CardManager

    private var selectedType = "ONLINE"
    private var isFormatting = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        setupTypeSelector()
        setupCardNumberFormat()
        setupExpiryFormat()

        binding.btnSaveCard.setOnClickListener {
            saveCard()
        }
    }

    private fun setupTypeSelector() {
        updateTypeUI("ONLINE")

        binding.btnTypeOnline.setOnClickListener {
            selectedType = "ONLINE"
            updateTypeUI("ONLINE")
        }

        binding.btnTypeBirbank.setOnClickListener {
            selectedType = "BIRBANK"
            updateTypeUI("BIRBANK")
        }
    }

    private fun updateTypeUI(activeType: String) {
        val pink = Color.parseColor("#E91E8C")
        val gray = Color.parseColor("#555555")

        if (activeType == "ONLINE") {
            binding.btnTypeOnline.setBackgroundResource(R.drawable.bg_active_filter_chip)
            binding.btnTypeBirbank.setBackgroundResource(R.drawable.bg_filter_chip)
            binding.rbTypeOnline.isChecked = true
            binding.rbTypeBirbank.isChecked = false
            getTextChild(binding.btnTypeOnline)?.setTextColor(pink)
            getTextChild(binding.btnTypeBirbank)?.setTextColor(gray)
        } else {
            binding.btnTypeBirbank.setBackgroundResource(R.drawable.bg_active_filter_chip)
            binding.btnTypeOnline.setBackgroundResource(R.drawable.bg_filter_chip)
            binding.rbTypeBirbank.isChecked = true
            binding.rbTypeOnline.isChecked = false
            getTextChild(binding.btnTypeBirbank)?.setTextColor(pink)
            getTextChild(binding.btnTypeOnline)?.setTextColor(gray)
        }
    }

    private fun getTextChild(layout: android.widget.LinearLayout): TextView? {
        for (i in 0 until layout.childCount) {
            val child = layout.getChildAt(i)
            if (child is TextView) return child
        }
        return null
    }

    private fun saveCard() {
        val cardNumber = binding.etCardNumber.text.toString().trim()
        val expiry = binding.etExpiry.text.toString().trim()
        val cvv = binding.etCvv.text.toString().trim()
        val holderName = binding.etHolderName.text.toString().trim()

        if (cardNumber.replace(" ", "").length < 16) {
            Toast.makeText(requireContext(), "Kart nömrəsi 16 rəqəm olmalıdır", Toast.LENGTH_SHORT).show()
            return
        }
        if (expiry.length < 5) {
            Toast.makeText(requireContext(), "AY/İL düzgün daxil edin (məs: 12/25)", Toast.LENGTH_SHORT).show()
            return
        }
        if (cvv.isEmpty()) {
            Toast.makeText(requireContext(), "CVV2 daxil edin", Toast.LENGTH_SHORT).show()
            return
        }

        val card = CardModel(
            cardNumber = cardNumber,
            expiry = expiry,
            cvv = cvv,
            holderName = holderName,
            cardType = selectedType
        )
        cardManager.saveCard(card)
        Toast.makeText(requireContext(), "Kart əlavə edildi", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
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
