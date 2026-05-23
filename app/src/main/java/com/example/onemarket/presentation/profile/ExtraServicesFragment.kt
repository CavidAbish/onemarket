package com.example.onemarket.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.onemarket.databinding.FragmentExtraServicesBinding

class ExtraServicesFragment : Fragment() {

    private var _binding: FragmentExtraServicesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExtraServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.itemBirBonus.setOnClickListener {
            Toast.makeText(requireContext(), "Bir Bonus", Toast.LENGTH_SHORT).show()
        }

        binding.itemSmartBonus.setOnClickListener {
            Toast.makeText(requireContext(), "Smart Bonus", Toast.LENGTH_SHORT).show()
        }

        binding.itemXaricden.setOnClickListener {
            Toast.makeText(requireContext(), "Xaricdən bağlamalar", Toast.LENGTH_SHORT).show()
        }

        binding.itemEdv.setOnClickListener {
            Toast.makeText(requireContext(), "ƏDV Geri Al (Kapital Bank)", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
