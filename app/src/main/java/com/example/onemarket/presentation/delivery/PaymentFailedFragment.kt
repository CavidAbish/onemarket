package com.example.onemarket.presentation.delivery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.onemarket.databinding.FragmentPaymentFailedBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentFailedFragment : Fragment() {

    private var _binding: FragmentPaymentFailedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPaymentFailedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Yenidən cəhd et — payment ekranına qayıt
        binding.btnRetry.setOnClickListener {
            findNavController().popBackStack()
        }

        // Bağla
        binding.btnClose.setOnClickListener {
            findNavController().popBackStack(
                com.example.onemarket.R.id.cartFragment, false
            )
        }

        // Davam et
        binding.btnContinue.setOnClickListener {
            findNavController().popBackStack(
                com.example.onemarket.R.id.cartFragment, false
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}