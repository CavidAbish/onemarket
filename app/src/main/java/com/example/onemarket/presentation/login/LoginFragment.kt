package com.example.onemarket.presentation.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Nömrə daxil edildikcə düyməni aktiv et
        binding.etPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val isValid = (s?.length ?: 0) >= 9
                binding.btnNext.backgroundTintList =
                    androidx.core.content.ContextCompat.getColorStateList(
                        requireContext(),
                        if (isValid) R.color.pink_main else android.R.color.darker_gray
                    )
                binding.btnNext.isEnabled = isValid
            }
        })

        // İrəli — sadəcə demo olaraq giriş simulyasiyası
        binding.btnNext.setOnClickListener {
            val phone = "+994 ${binding.etPhone.text}"
            userManager.saveUser("İstifadəçi", phone)
            Toast.makeText(requireContext(), "Xoş gəldiniz!", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        binding.btnBirId.setOnClickListener {
            Toast.makeText(requireContext(), "Bir ID ilə giriş", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}