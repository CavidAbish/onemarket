package com.example.onemarket.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.onemarket.data.local.CardManager
import com.example.onemarket.databinding.FragmentCardDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class CardDetailFragment : Fragment() {

    private var _binding: FragmentCardDetailBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var cardManager: CardManager

    private val args: CardDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCardDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        val card = cardManager.getCardById(args.cardId)
        if (card == null) {
            findNavController().popBackStack()
            return
        }

        // Visual card
        binding.tvDetailTypeLabel.text = card.typeLabel
        binding.tvDetailCardNumber.text = card.maskedNumber
        binding.tvDetailHolder.text = card.holderName.ifEmpty { "—" }
        binding.tvDetailExpiry.text = card.expiry

        // Info rows
        binding.tvDetailType.text = card.typeLabel
        binding.tvDetailMasked.text = card.maskedNumber
        binding.tvDetailExpiryInfo.text = card.expiry
        val dateStr = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(card.addedAt))
        binding.tvDetailAddedAt.text = dateStr

        // Kart nömrəsinə clicklədikdə tam nömrəni göstər / gizlət
        var fullNumberVisible = false
        val maskToggle = {
            fullNumberVisible = !fullNumberVisible
            val displayed = if (fullNumberVisible) card.cardNumber else card.maskedNumber
            binding.tvDetailMasked.text = displayed
            binding.tvDetailCardNumber.text = displayed
        }
        binding.tvDetailMasked.setOnClickListener { maskToggle() }
        binding.tvDetailCardNumber.setOnClickListener { maskToggle() }

        // CVV — clicklədikdə göstər / gizlət
        var cvvVisible = false
        binding.tvDetailCvv.setOnClickListener {
            cvvVisible = !cvvVisible
            binding.tvDetailCvv.text = if (cvvVisible && card.cvv.isNotEmpty()) card.cvv else "•••"
        }

        binding.btnDeleteCard.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Kartı sil")
                .setMessage("Bu kartı silmək istədiyinizdən əminsiniz?")
                .setPositiveButton("Sil") { _, _ ->
                    cardManager.deleteCard(card.id)
                    Toast.makeText(requireContext(), "Kart silindi", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                .setNegativeButton("Ləğv et", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
