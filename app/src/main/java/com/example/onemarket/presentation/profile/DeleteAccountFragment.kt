package com.example.onemarket.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.onemarket.databinding.FragmentDeleteAccountBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteAccountFragment : Fragment() {

    private var _binding: FragmentDeleteAccountBinding? = null
    private val binding get() = _binding!!


    private data class Country(val flag: String, val label: String, val code: String)

    private val countries = listOf(
        Country("🇦🇿", "+994", "994"),
        Country("🇷🇺", "+7",   "7"),
        Country("🇹🇷", "+90",  "90"),
        Country("🇺🇸", "+1",   "1"),
        Country("🇬🇧", "+44",  "44"),
        Country("🇩🇪", "+49",  "49"),
        Country("🇬🇪", "+995", "995"),
        Country("🇺🇦", "+380", "380"),
        Country("🇦🇪", "+971", "971"),
        Country("🇰🇿", "+7",   "7"),
        Country("🇫🇷", "+33",  "33"),
        Country("🇮🇹", "+39",  "39")
    )

    private var selectedCountry = countries[0]   // Default: Azerbaijan

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeleteAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }


        binding.btnCountry.setOnClickListener { showCountryPicker() }

        binding.btnIreli.setOnClickListener {
            val entered = binding.etPhone.text?.toString()?.trim() ?: ""
            if (entered.length < 7) {
                Toast.makeText(requireContext(), "Telefon nömrəsini düzgün daxil edin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val fullPhone = "+${selectedCountry.code}$entered"
            findNavController().navigate(
                DeleteAccountFragmentDirections
                    .actionDeleteAccountFragmentToDeleteAccountOtpFragment(fullPhone)
            )
        }

        binding.btnImtina.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun showCountryPicker() {
        val items = countries.map { "${it.flag}  ${it.label}" }.toTypedArray()
        val currentIndex = countries.indexOf(selectedCountry).coerceAtLeast(0)

        AlertDialog.Builder(requireContext())
            .setTitle("Ölkə kodu seçin")
            .setSingleChoiceItems(items, currentIndex) { dialog, which ->
                selectedCountry = countries[which]
                binding.tvCountryFlag.text = selectedCountry.flag
                binding.tvCountryCode.text = " ${selectedCountry.label}"
                dialog.dismiss()
            }
            .setNegativeButton("Ləğv et", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
