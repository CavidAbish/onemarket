package com.example.onemarket.presentation.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onemarket.R
import com.example.onemarket.data.local.UserManager
import com.example.onemarket.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var userManager: UserManager

    private lateinit var countryAdapter: CountryAdapter
    private var selectedCountry = CountryData.countries[0] // Default: Azərbaycan

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCountryPicker()
        setupPhoneInput()

        // Seçilmiş ölkəni göstər
        updateSelectedCountry(selectedCountry)

        // Geri
        binding.btnBack.setOnClickListener {
            if (binding.countryPickerOverlay.visibility == View.VISIBLE) {
                binding.countryPickerOverlay.visibility = View.GONE
            } else {
                findNavController().popBackStack()
            }
        }

        // Ölkə seçim açılsın
        binding.btnCountry.setOnClickListener {
            binding.countryPickerOverlay.visibility = View.VISIBLE
            binding.etCountrySearch.requestFocus()
            showKeyboard(binding.etCountrySearch)
        }

        // Ölkə siyahısını bağla
        binding.btnCloseCountry.setOnClickListener {
            binding.countryPickerOverlay.visibility = View.GONE
            hideKeyboard()
        }

        // İrəli — demo giriş
        binding.btnNext.setOnClickListener {
            val phone = "${selectedCountry.code} ${binding.etPhone.text}"
            userManager.saveUser("İstifadəçi", phone)
            Toast.makeText(requireContext(), "Xoş gəldiniz!", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        // Daxil ol
        binding.btnLogin.setOnClickListener {
            val phone = "${selectedCountry.code} ${binding.etPhone.text}"
            userManager.saveUser("İstifadəçi", phone)
            Toast.makeText(requireContext(), "Xoş gəldiniz!", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private fun setupCountryPicker() {
        countryAdapter = CountryAdapter { country ->
            selectedCountry = country
            updateSelectedCountry(country)
            binding.countryPickerOverlay.visibility = View.GONE
            hideKeyboard()
        }

        binding.recyclerViewCountries.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCountries.adapter = countryAdapter
        countryAdapter.submitList(CountryData.countries)

        // Ölkə axtarışı
        binding.etCountrySearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.lowercase() ?: ""
                val filtered = CountryData.countries.filter {
                    it.name.lowercase().contains(query) || it.code.contains(query)
                }
                countryAdapter.submitList(filtered)
            }
        })
    }

    private fun setupPhoneInput() {
        binding.etPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val isValid = (s?.length ?: 0) >= 7
                binding.btnNext.isEnabled = isValid
                binding.btnNext.backgroundTintList = ContextCompat.getColorStateList(
                    requireContext(),
                    if (isValid) R.color.pink_main else android.R.color.darker_gray
                )
            }
        })
    }

    private fun updateSelectedCountry(country: Country) {
        binding.tvCountryFlag.text = country.flag
        binding.tvCountryCode.text = country.code
    }

    private fun showKeyboard(view: View) {
        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE)
                as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE)
                as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}